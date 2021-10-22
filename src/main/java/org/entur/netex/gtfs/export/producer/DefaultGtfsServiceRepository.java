/*
 *
 *  * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  *   https://joinup.ec.europa.eu/software/page/eupl
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *  *
 *
 */

package org.entur.netex.gtfs.export.producer;

import org.entur.netex.gtfs.export.exception.GtfsExportException;
import org.entur.netex.gtfs.export.model.GtfsService;
import org.entur.netex.gtfs.export.model.ServiceCalendarPeriod;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.apache.commons.lang3.StringUtils;
import org.rutebanken.netex.model.DayOfWeekEnumeration;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.EntityStructure;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.PropertyOfDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Create and store the GTFS services for the current dataset.
 * GTFS services are created while iterating through ServiceJourneys and DatedServiceJourneys.
 * The GTFS services are de-duplicated by creating a unique ID per set of DayTypes (trips based on ServiceJourneys) or set of OperatingDays (trips based on DatedServiceJourneys)
 */
public class DefaultGtfsServiceRepository implements GtfsServiceRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGtfsServiceRepository.class);


    // No restrictions in GTFS spec, but restricted to suit clients
    private static final int MAX_SERVICE_ID_CHARS = 256;

    private final String codespace;
    private final NetexDatasetRepository netexDatasetRepository;
    private final Map<String, GtfsService> gtfsServices;

    public DefaultGtfsServiceRepository(String codespace, NetexDatasetRepository netexDatasetRepository) {
        this.netexDatasetRepository = netexDatasetRepository;
        this.gtfsServices = new HashMap<>();
        this.codespace = codespace;
    }

    @Override
    public Collection<GtfsService> getAllServices() {
        return gtfsServices.values();
    }

    @Override
    public GtfsService getServiceForDayTypes(Set<DayType> dayTypes) {
        String serviceId = getServiceIdForDayTypes(dayTypes);
        return gtfsServices.computeIfAbsent(serviceId, s -> createGtfsServiceForDayTypes(dayTypes, serviceId));
    }

    @Override
    public GtfsService getServiceForOperatingDays(Set<OperatingDay> operatingDays) {
        String serviceId = getServiceIdForOperatingDays(operatingDays);
        return gtfsServices.computeIfAbsent(serviceId, s -> createGtfsServiceForOperatingDays(operatingDays, serviceId));
    }

    private String getServiceIdForDayTypes(Set<DayType> dayTypes) {
        String serviceId = codespace + ":DayType:" + dayTypes.stream().map(EntityStructure::getId).map(DefaultGtfsServiceRepository::splitId).sorted().collect(Collectors.joining("-"));
        if (serviceId.length() > MAX_SERVICE_ID_CHARS) {
            serviceId = truncateServiceId(serviceId);
        }
        return serviceId;
    }

    private String getServiceIdForOperatingDays(Set<OperatingDay> operatingDays) {
        String serviceId = codespace + ":OperatingDay:" + operatingDays.stream().map(EntityStructure::getId).map(DefaultGtfsServiceRepository::splitId).sorted().collect(Collectors.joining("-"));
        if (serviceId.length() > MAX_SERVICE_ID_CHARS) {
            serviceId = truncateServiceId(serviceId);
        }
        return serviceId;
    }

    /**
     * Truncate long IDs and add a hash to preserve uniqueness.
     * @param serviceId the GTFS service id
     * @return a truncated GTFS ID if the service id length is greater than {@link #MAX_SERVICE_ID_CHARS}
     */
    private static String truncateServiceId(String serviceId) {
        String tooLongPart = serviceId.substring(MAX_SERVICE_ID_CHARS - 10);
        serviceId = serviceId.replace(tooLongPart, StringUtils.truncate("" + tooLongPart.hashCode(), 10));
        return serviceId;
    }


    private static String splitId(String id) {
        return id.split(":")[2];
    }


    private GtfsService createGtfsServiceForDayTypes(Set<DayType> dayTypes, String serviceId) {
        int nbPeriods = countPeriods(dayTypes);
        if (nbPeriods == 0) {
            return createGtfsServiceForIndividualDates(dayTypes, serviceId);
        } else if (nbPeriods == 1) {
            return createGtfsServiceForOnePeriodAndIndividualDates(dayTypes, serviceId);
        } else {
            return createGtfsServiceForMultiplePeriodsAndIndividualDates(dayTypes, serviceId);
        }
    }

    private static GtfsService createGtfsServiceForOperatingDays(Set<OperatingDay> operatingDays, String serviceId) {
        LOGGER.debug("Creating GTFS Service for operating days for serviceId {}", serviceId);
        GtfsService gtfsService = new GtfsService(serviceId);
        operatingDays.forEach(operatingDay -> gtfsService.addIncludedDate(operatingDay.getCalendarDate()));
        return gtfsService;
    }

    private GtfsService createGtfsServiceForIndividualDates(Set<DayType> dayTypes, String serviceId) {
        LOGGER.debug("Creating GTFS Service for individual dates for serviceId {}", serviceId);
        GtfsService gtfsService = new GtfsService(serviceId);
        dayTypes.stream()
                .map(netexDatasetRepository::getDayTypeAssignmentsByDayType)
                .flatMap(Collection::stream)
                .forEach(dayTypeAssignment -> addIndividualDate(gtfsService, dayTypeAssignment));

        // Remove the excluded dates from the included dates and remove all the excluded dates.
        // Since there is no period but only individual dates, it is sufficient to list the included dates.
        // Date exclusion has precedence over date inclusion.
        gtfsService.removeIncludedDates(gtfsService.getExcludedDates());
        gtfsService.removeAllExcludedDates();

        return gtfsService;
    }

    private GtfsService createGtfsServiceForOnePeriodAndIndividualDates(Set<DayType> dayTypes, String serviceId) {
        LOGGER.debug("Creating GTFS Service for one period and individual dates for serviceId {}", serviceId);
        GtfsService gtfsService = new GtfsService(serviceId);
        DayTypeAssignment dayTypeAssignmentWithPeriod = dayTypes.stream()
                .map(netexDatasetRepository::getDayTypeAssignmentsByDayType)
                .flatMap(Collection::stream)
                .filter(dta -> dta.getOperatingPeriodRef() != null)
                .findFirst()
                .orElseThrow();

        DayType dayTypeWithAPeriod = netexDatasetRepository.getDayTypeByDayTypeAssignment(dayTypeAssignmentWithPeriod);
        OperatingPeriod operatingPeriod = netexDatasetRepository.getOperatingPeriodByDayTypeAssignment(dayTypeAssignmentWithPeriod);
        ServiceCalendarPeriod serviceCalendarPeriod = new ServiceCalendarPeriod(operatingPeriod.getFromDate(), operatingPeriod.getToDate());
        serviceCalendarPeriod.setDaysOfWeek(getNetexDaysOfWeek(dayTypeWithAPeriod));
        gtfsService.setServiceCalendarPeriod(serviceCalendarPeriod);

        dayTypes.stream()
                .map(netexDatasetRepository::getDayTypeAssignmentsByDayType)
                .flatMap(Collection::stream)
                .filter(dta -> dta.getOperatingPeriodRef() == null)
                .forEach(dayTypeAssignment -> addIndividualDate(gtfsService, dayTypeAssignment));

        // Remove included dates that are also listed in the excluded dates
        // Date exclusion has precedence over date inclusion.
        gtfsService.removeIncludedDates(gtfsService.getExcludedDates());

        return gtfsService;
    }

    private GtfsService createGtfsServiceForMultiplePeriodsAndIndividualDates(Set<DayType> dayTypes, String serviceId) {
        LOGGER.debug("Creating GTFS Service for multiple periods and individual dates  for serviceId {}", serviceId);
        GtfsService gtfsService = new GtfsService(serviceId);
        for (DayType dayType : dayTypes) {
            Set<DayOfWeek> daysOfWeek = getDaysOfWeek(dayType);
            for (DayTypeAssignment dayTypeAssignment : netexDatasetRepository.getDayTypeAssignmentsByDayType(dayType)) {
                if (dayTypeAssignment.getOperatingPeriodRef() != null) {
                    OperatingPeriod operatingPeriod = netexDatasetRepository.getOperatingPeriodByDayTypeAssignment(dayTypeAssignment);
                    for (LocalDateTime date = operatingPeriod.getFromDate(); date.isBefore(operatingPeriod.getToDate()) || date.equals(operatingPeriod.getToDate()); date = date.plusDays(1)) {
                        if (isActiveDate(date, daysOfWeek)) {
                            gtfsService.addIncludedDate(date);
                        }
                    }
                }
            }
        }

        dayTypes.stream()
                .map(netexDatasetRepository::getDayTypeAssignmentsByDayType)
                .flatMap(Collection::stream)
                .filter(dta -> dta.getOperatingPeriodRef() == null)
                .forEach(dayTypeAssignment -> addIndividualDate(gtfsService, dayTypeAssignment));

        // remove the excluded dates from the included dates and remove all the excluded dates.
        // Since there is no period but only individual dates, it is sufficient to list the included dates.
        // Date exclusion has precedence over date inclusion.
        gtfsService.removeIncludedDates(gtfsService.getExcludedDates());
        gtfsService.removeAllExcludedDates();

        return gtfsService;
    }

    private void addIndividualDate(GtfsService gtfsService, DayTypeAssignment dayTypeAssignment) {
        LocalDateTime date;
        if (dayTypeAssignment.getOperatingDayRef() != null) {
            OperatingDay operatingDay = netexDatasetRepository.getOperatingDayByDayTypeAssignment(dayTypeAssignment);
            date = operatingDay.getCalendarDate();
        } else if (dayTypeAssignment.getDate() != null) {
            date = dayTypeAssignment.getDate();
        } else {
            throw new GtfsExportException("Both Date and OperatingDay are undefined on DayTypeAssignment " + dayTypeAssignment.getId());
        }
        if (dayTypeAssignment.isIsAvailable() != null && !dayTypeAssignment.isIsAvailable()) {
            gtfsService.addExcludedDate(date);
        } else {
            gtfsService.addIncludedDate(date);
        }
    }

    private static List<DayOfWeekEnumeration> getNetexDaysOfWeek(DayType dayType) {
        if (dayType.getProperties() != null && dayType.getProperties().getPropertyOfDay() != null) {
            for (PropertyOfDay propertyOfDay : dayType.getProperties().getPropertyOfDay()) {
                if (propertyOfDay.getDaysOfWeek() != null && !propertyOfDay.getDaysOfWeek().isEmpty()) {
                    return propertyOfDay.getDaysOfWeek();
                }
            }
        }
        return null;
    }

    private static Set<DayOfWeek> getDaysOfWeek(DayType dayType) {
        List<DayOfWeekEnumeration> netexDaysOfWeek = getNetexDaysOfWeek(dayType);
        if (netexDaysOfWeek == null) {
            return null;
        }
        return netexDaysOfWeek.stream().map(dayOfWeekEnumeration -> {
            if (DayOfWeekEnumeration.MONDAY == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.MONDAY);
            } else if (DayOfWeekEnumeration.TUESDAY == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.TUESDAY);
            } else if (DayOfWeekEnumeration.WEDNESDAY == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.WEDNESDAY);
            } else if (DayOfWeekEnumeration.THURSDAY == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.THURSDAY);
            } else if (DayOfWeekEnumeration.FRIDAY == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.FRIDAY);
            } else if (DayOfWeekEnumeration.SATURDAY == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.SATURDAY);
            } else if (DayOfWeekEnumeration.SUNDAY == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.SUNDAY);
            } else if (DayOfWeekEnumeration.WEEKDAYS == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
            } else if (DayOfWeekEnumeration.WEEKEND == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            } else if (DayOfWeekEnumeration.EVERYDAY == dayOfWeekEnumeration) {
                return List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            } else {
                throw new GtfsExportException("Unsupported day of week: " + dayOfWeekEnumeration);
            }

        }).flatMap(Collection::stream).collect(Collectors.toSet());
    }


    private static boolean isActiveDate(LocalDateTime date, Set<DayOfWeek> daysOfWeek) {
        if (daysOfWeek == null) {
            return true;
        }
        return daysOfWeek.contains(date.getDayOfWeek());
    }


    private int countPeriods(Set<DayType> dayTypes) {
        return dayTypes.stream().map(dayType -> netexDatasetRepository.getDayTypeAssignmentsByDayType(dayType)
                .stream()
                .filter(dayTypeAssignment -> dayTypeAssignment.getOperatingPeriodRef() != null)
                .count()).mapToInt(Long::intValue).sum();
    }


}

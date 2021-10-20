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

import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.rutebanken.netex.model.DayOfWeekEnumeration;

import java.util.Collection;

public class DefaultServiceCalendarProducer implements ServiceCalendarProducer {

    private final Agency agency;

    public DefaultServiceCalendarProducer(GtfsDatasetRepository gtfsDatasetRepository) {
        this.agency = gtfsDatasetRepository.getDefaultAgency();
    }

    @Override
    public ServiceCalendar produce(String serviceId, ServiceDate startDate, ServiceDate endDate, Collection<DayOfWeekEnumeration> daysOfWeeks) {
        ServiceCalendar serviceCalendar = new ServiceCalendar();

        AgencyAndId agencyAndId = new AgencyAndId();
        agencyAndId.setId(serviceId);
        agencyAndId.setAgencyId(agency.getId());
        serviceCalendar.setServiceId(agencyAndId);
        serviceCalendar.setStartDate(startDate);
        serviceCalendar.setEndDate(endDate);

        if (daysOfWeeks.isEmpty()) {
            serviceCalendar.setMonday(ServiceCalendarProducer.SERVICE_AVAILABLE);
            serviceCalendar.setTuesday(ServiceCalendarProducer.SERVICE_AVAILABLE);
            serviceCalendar.setWednesday(ServiceCalendarProducer.SERVICE_AVAILABLE);
            serviceCalendar.setThursday(ServiceCalendarProducer.SERVICE_AVAILABLE);
            serviceCalendar.setFriday(ServiceCalendarProducer.SERVICE_AVAILABLE);
            serviceCalendar.setSaturday(ServiceCalendarProducer.SERVICE_AVAILABLE);
            serviceCalendar.setSunday(ServiceCalendarProducer.SERVICE_AVAILABLE);
        } else {
            serviceCalendar.setMonday(daysOfWeeks.contains(DayOfWeekEnumeration.MONDAY) ? ServiceCalendarProducer.SERVICE_AVAILABLE : ServiceCalendarProducer.SERVICE_UNAVAILABLE);
            serviceCalendar.setTuesday(daysOfWeeks.contains(DayOfWeekEnumeration.TUESDAY) ? ServiceCalendarProducer.SERVICE_AVAILABLE : ServiceCalendarProducer.SERVICE_UNAVAILABLE);
            serviceCalendar.setWednesday(daysOfWeeks.contains(DayOfWeekEnumeration.WEDNESDAY) ? ServiceCalendarProducer.SERVICE_AVAILABLE : ServiceCalendarProducer.SERVICE_UNAVAILABLE);
            serviceCalendar.setThursday(daysOfWeeks.contains(DayOfWeekEnumeration.THURSDAY) ? ServiceCalendarProducer.SERVICE_AVAILABLE : ServiceCalendarProducer.SERVICE_UNAVAILABLE);
            serviceCalendar.setFriday(daysOfWeeks.contains(DayOfWeekEnumeration.FRIDAY) ? ServiceCalendarProducer.SERVICE_AVAILABLE : ServiceCalendarProducer.SERVICE_UNAVAILABLE);
            serviceCalendar.setSaturday(daysOfWeeks.contains(DayOfWeekEnumeration.SATURDAY) ? ServiceCalendarProducer.SERVICE_AVAILABLE : ServiceCalendarProducer.SERVICE_UNAVAILABLE);
            serviceCalendar.setSunday(daysOfWeeks.contains(DayOfWeekEnumeration.SUNDAY) ? ServiceCalendarProducer.SERVICE_AVAILABLE : ServiceCalendarProducer.SERVICE_UNAVAILABLE);

            if (daysOfWeeks.contains(DayOfWeekEnumeration.WEEKDAYS)) {
                serviceCalendar.setMonday(ServiceCalendarProducer.SERVICE_AVAILABLE);
                serviceCalendar.setTuesday(ServiceCalendarProducer.SERVICE_AVAILABLE);
                serviceCalendar.setWednesday(ServiceCalendarProducer.SERVICE_AVAILABLE);
                serviceCalendar.setThursday(ServiceCalendarProducer.SERVICE_AVAILABLE);
                serviceCalendar.setFriday(ServiceCalendarProducer.SERVICE_AVAILABLE);
            }

            if (daysOfWeeks.contains(DayOfWeekEnumeration.WEEKEND)) {
                serviceCalendar.setSaturday(ServiceCalendarProducer.SERVICE_AVAILABLE);
                serviceCalendar.setSunday(ServiceCalendarProducer.SERVICE_AVAILABLE);
            }
        }

        return serviceCalendar;
    }
}

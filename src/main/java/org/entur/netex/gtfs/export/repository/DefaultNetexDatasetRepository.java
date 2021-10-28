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

package org.entur.netex.gtfs.export.repository;

import org.entur.netex.gtfs.export.exception.DefaultTimeZoneException;
import org.entur.netex.gtfs.export.exception.GtfsExportException;
import org.entur.netex.gtfs.export.exception.QuayNotFoundException;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.index.impl.NetexEntitiesIndexImpl;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.LocaleStructure;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.ServiceLink;
import org.rutebanken.netex.model.VersionFrameDefaultsStructure;
import org.rutebanken.netex.model.VersionFrame_VersionStructure;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultNetexDatasetRepository implements NetexDatasetRepository {

    private final NetexEntitiesIndex netexEntitiesIndex;
    private volatile String timezone;

    public DefaultNetexDatasetRepository() {
        this.netexEntitiesIndex = new NetexEntitiesIndexImpl();
    }

    @Override
    public NetexEntitiesIndex getIndex() {
        return netexEntitiesIndex;
    }

    @Override
    public OperatingDay getOperatingDayByDayTypeAssignment(DayTypeAssignment dayTypeAssignment) {
        if (dayTypeAssignment.getOperatingDayRef() == null) {
            throw new GtfsExportException("OperatingDay undefined for DayTypeAssignment " + dayTypeAssignment.getId());
        }
        OperatingDay operatingDay = netexEntitiesIndex.getOperatingDayIndex().get(dayTypeAssignment.getOperatingDayRef().getRef());
        if (operatingDay == null) {
            throw new GtfsExportException("OperatingDay not found: " + dayTypeAssignment.getOperatingDayRef());
        }
        return operatingDay;
    }

    @Override
    public OperatingPeriod getOperatingPeriodByDayTypeAssignment(DayTypeAssignment dayTypeAssignment) {
        if (dayTypeAssignment.getOperatingPeriodRef() == null) {
            throw new GtfsExportException("OperatingPeriod undefined for DayTypeAssignment " + dayTypeAssignment.getId());
        }
        OperatingPeriod operatingPeriod = netexEntitiesIndex.getOperatingPeriodIndex().get(dayTypeAssignment.getOperatingPeriodRef().getRef());
        if (operatingPeriod == null) {
            throw new GtfsExportException("OperatingPeriod not found: " + dayTypeAssignment.getOperatingPeriodRef());
        }
        return operatingPeriod;
    }

    @Override
    public DayType getDayTypeByDayTypeAssignment(DayTypeAssignment dayTypeAssignment) {
        String dayTypeId = dayTypeAssignment.getDayTypeRef().getValue().getRef();
        DayType dayType = netexEntitiesIndex.getDayTypeIndex().get(dayTypeId);
        if (dayType == null) {
            throw new GtfsExportException("DayType not found: " + dayTypeId);
        }
        return dayType;
    }

    @Override
    public String getTimeZone() {
        if (timezone == null) {
            Set<String> timeZones = netexEntitiesIndex.getCompositeFrames()
                    .stream()
                    .map(VersionFrame_VersionStructure::getFrameDefaults)
                    .filter(Objects::nonNull)
                    .map(VersionFrameDefaultsStructure::getDefaultLocale)
                    .filter(Objects::nonNull)
                    .map(LocaleStructure::getTimeZone)
                    .collect(Collectors.toSet());

            if (timeZones.size() > 1) {
                throw new DefaultTimeZoneException("The dataset contains more than one default timezone");
            }
            timezone = timeZones.stream().findFirst().orElseThrow(() -> new DefaultTimeZoneException("The dataset does not contain a default timezone"));
        }
        return timezone;
    }


    @Override
    public String getAuthorityIdForLine(Line line) {
        Network network = findNetwork(line.getRepresentedByGroupRef().getRef());
        return network.getTransportOrganisationRef().getValue().getRef();
    }

    /**
     * Return the network referenced by the <RepresentedByGroupRef>.
     * RepresentedByGroupRef can reference a network either directly or indirectly (through a group of lines)
     *
     * @param networkOrGroupOfLinesRef reference to a Network or a group of lines.
     * @return the network itself or the network to which the group of lines belongs to.
     */
    private Network findNetwork(String networkOrGroupOfLinesRef) {
        Network network = netexEntitiesIndex.getNetworkIndex().get(networkOrGroupOfLinesRef);
        if (network != null) {
            return network;
        } else {
            return netexEntitiesIndex.getNetworkIndex()
                    .getAll()
                    .stream()
                    .filter(n -> n.getGroupsOfLines() != null)
                    .filter(n -> n.getGroupsOfLines()
                            .getGroupOfLines()
                            .stream()
                            .anyMatch(groupOfLine -> groupOfLine.getId().equals(networkOrGroupOfLinesRef)))
                    .findFirst()
                    .orElseThrow();
        }
    }

    @Override
    public Collection<Line> getLines() {
        return netexEntitiesIndex.getLineIndex().getAll();
    }

    @Override
    public Collection<ServiceJourney> getServiceJourneys() {
        return netexEntitiesIndex.getServiceJourneyIndex().getAll();
    }

    @Override
    public Collection<ServiceJourneyInterchange> getServiceJourneyInterchanges() {
        return netexEntitiesIndex.getServiceJourneyInterchangeIndex().getAll();
    }

    @Override
    public Collection<DayTypeAssignment> getDayTypeAssignmentsByDayType(DayType dayType) {
        return netexEntitiesIndex.getDayTypeAssignmentsByDayTypeIdIndex().get(dayType.getId());
    }


    @Override
    public Authority getAuthorityById(String authorityId) {
        return netexEntitiesIndex.getAuthorityIndex().get(authorityId);
    }

    @Override
    public ServiceJourney getServiceJourneyById(String serviceJourneyId) {
        return netexEntitiesIndex.getServiceJourneyIndex().get(serviceJourneyId);
    }

    @Override
    public String getFlexibleStopPlaceIdByScheduledStopPointId(String scheduledStopPointId) {
        return netexEntitiesIndex.getFlexibleStopPlaceIdByStopPointRefIndex().get(scheduledStopPointId);
    }

    @Override
    public String getQuayIdByScheduledStopPointId(String scheduledStopPointId) {
        String quayId = netexEntitiesIndex.getQuayIdByStopPointRefIndex().get(scheduledStopPointId);
        if (quayId == null) {
            throw new QuayNotFoundException("Could not find Quay id for scheduled stop point id " + scheduledStopPointId);
        }
        return quayId;
    }


    @Override
    public JourneyPattern getJourneyPatternById(String journeyPatternId) {
        return netexEntitiesIndex.getJourneyPatternIndex().get(journeyPatternId);
    }

    @Override
    public Collection<ServiceJourney> getServiceJourneysByJourneyPattern(JourneyPattern journeyPattern) {
        return getServiceJourneys()
                .stream()
                .filter(serviceJourney -> serviceJourney.getJourneyPatternRef().getValue().getRef().equals(journeyPattern.getId()))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<org.rutebanken.netex.model.Route> getRoutesByLine(Line line) {
        return netexEntitiesIndex.getRouteIndex().getAll()
                .stream()
                .filter(route -> route.getLineRef().getValue().getRef().equals(line.getId()))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<JourneyPattern> getJourneyPatternsByRoute(org.rutebanken.netex.model.Route route) {
        return netexEntitiesIndex.getJourneyPatternIndex().getAll()
                .stream()
                .filter(journeyPattern -> journeyPattern.getRouteRef().getRef().equals(route.getId()))
                .collect(Collectors.toSet());
    }

    @Override
    public ServiceLink getServiceLinkById(String serviceLinkId) {
        return netexEntitiesIndex.getServiceLinkIndex().get(serviceLinkId);
    }


    @Override
    public DestinationDisplay getDestinationDisplayById(String destinationDisplayId) {
        return netexEntitiesIndex.getDestinationDisplayIndex().get(destinationDisplayId);
    }

    @Override
    public Collection<DatedServiceJourney> getDatedServiceJourneysByServiceJourneyId(String serviceJourneyId) {
        return netexEntitiesIndex.getDatedServiceJourneyByServiceJourneyRefIndex().get(serviceJourneyId);
    }

    @Override
    public DayType getDayTypeById(String dayTypeId) {
        DayType dayType = netexEntitiesIndex.getDayTypeIndex().get(dayTypeId);
        if (dayType == null) {
            throw new GtfsExportException("Could not find DayType with id " + dayTypeId);
        }
        return dayType;
    }

    @Override
    public OperatingDay getOperatingDayById(String operatingDayId) {
        OperatingDay operatingDay = netexEntitiesIndex.getOperatingDayIndex().get(operatingDayId);
        if (operatingDay == null) {
            throw new GtfsExportException("Could not find OperatingDay with id " + operatingDayId);
        }
        return operatingDay;
    }


}

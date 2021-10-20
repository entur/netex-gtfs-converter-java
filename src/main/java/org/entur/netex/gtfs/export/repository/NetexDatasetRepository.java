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

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.ServiceLink;

import java.util.Collection;

/**
 * Repository giving read access to the input NeTEx dataset.
 */
public interface NetexDatasetRepository {

    NetexEntitiesIndex getIndex();

    Collection<ServiceJourneyInterchange> getServiceJourneyInterchanges();

    Collection<DayTypeAssignment> getDayTypeAssignmentsByDayType(DayType dayType);

    OperatingDay getOperatingDayByDayTypeAssignment(DayTypeAssignment dayTypeAssignment);

    OperatingPeriod getOperatingPeriodByDayTypeAssignment(DayTypeAssignment dayTypeAssignment);

    DayType getDayTypeByDayTypeAssignment(DayTypeAssignment dayTypeAssignment);

    /**
     * Return the dataset default timezone
     * This is the timezone set at the CompositeFrame level.
     *
     * @return the dataset default timezone
     * @throws org.entur.netex.gtfs.export.exception.GtfsExportException if there is no default timezone or if there is more than one default timezone.
     */
    String getTimeZone();

    /**
     * Return the authority id for a given line.
     * This is the authority of the network or group of lines referenced by the line.
     *
     * @param line a NeTEx line
     * @return the line authority
     */
    String getAuthorityIdForLine(Line line);

    Collection<Line> getLines();

    Authority getAuthorityById(String authorityId);

    ServiceJourney getServiceJourneyById(String serviceJourneyId);

    String getFlexibleStopPlaceIdByScheduledStopPointId(String scheduledStopPointId);

    String getQuayIdByScheduledStopPointId(String scheduledStopPointId);

    Collection<ServiceJourney> getServiceJourneys();

    JourneyPattern getJourneyPatternById(String journeyPatternId);

    Collection<ServiceJourney> getServiceJourneysByJourneyPattern(JourneyPattern journeyPattern);

    Collection<Route> getRoutesByLine(Line line);

    Collection<JourneyPattern> getJourneyPatternsByRoute(Route route);

    ServiceLink getServiceLinkById(String serviceLinkId);

    DestinationDisplay getDestinationDisplayById(String destinationDisplayId);

    DayType getDayTypeById(String dayTypeId);

    Collection<DatedServiceJourney> getDatedServiceJourneysByServiceJourneyId(String serviceJourneyId);

    OperatingDay getOperatingDayById(String operatingDayId);

}

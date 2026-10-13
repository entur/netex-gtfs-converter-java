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

import java.util.Collection;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex2.model.Authority;
import org.entur.netex2.model.DatedServiceJourney;
import org.entur.netex2.model.DayType;
import org.entur.netex2.model.DayTypeAssignment;
import org.entur.netex2.model.DestinationDisplay;
import org.entur.netex2.model.JourneyPattern;
import org.entur.netex2.model.Line;
import org.entur.netex2.model.OperatingDay;
import org.entur.netex2.model.OperatingPeriod;
import org.entur.netex2.model.Route;
import org.entur.netex2.model.ServiceJourney;
import org.entur.netex2.model.ServiceJourneyInterchange;
import org.entur.netex2.model.ServiceLink;

/**
 * Repository giving read access to the input NeTEx dataset.
 */
public interface NetexDatasetRepository {
  NetexEntitiesIndex getIndex();

  Collection<ServiceJourneyInterchange> getServiceJourneyInterchanges();

  Collection<DayTypeAssignment> getDayTypeAssignmentsByDayType(DayType dayType);

  OperatingDay getOperatingDayByDayTypeAssignment(
    DayTypeAssignment dayTypeAssignment
  );

  OperatingPeriod getOperatingPeriodByDayTypeAssignment(
    DayTypeAssignment dayTypeAssignment
  );

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

  String getFlexibleStopPlaceIdByScheduledStopPointId(
    String scheduledStopPointId
  );

  String getQuayIdByScheduledStopPointId(String scheduledStopPointId);

  Collection<ServiceJourney> getServiceJourneys();

  JourneyPattern getJourneyPatternById(String journeyPatternId);

  Collection<ServiceJourney> getServiceJourneysByJourneyPattern(
    JourneyPattern journeyPattern
  );

  Collection<Route> getRoutesByLine(Line line);

  Collection<JourneyPattern> getJourneyPatternsByRoute(Route route);

  ServiceLink getServiceLinkById(String serviceLinkId);

  DestinationDisplay getDestinationDisplayById(String destinationDisplayId);

  DayType getDayTypeById(String dayTypeId);

  Collection<DatedServiceJourney> getDatedServiceJourneysByServiceJourneyId(
    String serviceJourneyId
  );

  OperatingDay getOperatingDayById(String operatingDayId);
}

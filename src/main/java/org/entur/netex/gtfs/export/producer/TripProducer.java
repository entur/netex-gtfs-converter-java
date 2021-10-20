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

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.ServiceJourney;


/**
 * Produce a GTFS Trip.
 */
public interface TripProducer {

    String GTFS_DIRECTION_OUTBOUND = "0";
    String GTFS_DIRECTION_INBOUND = "1";

    /**
     * Return a GTFS Trip corresponding to the ServiceJourney or null if the ServiceJourney cannot be converted into a valid GTFS trip.
     *
     * @param serviceJourney            the NeTEx service journey
     * @param netexRoute                the NeTEx route
     * @param gtfsRoute                 the GTFS route
     * @param shapeId                   the optional shape id
     * @param initialDestinationDisplay the initial destination display.
     * @return a GTFS Trip corresponding to the ServiceJourney or null if the ServiceJourney cannot be converted into a valid GTFS trip.
     */
    Trip produce(ServiceJourney serviceJourney, Route netexRoute, org.onebusaway.gtfs.model.Route gtfsRoute, AgencyAndId shapeId, DestinationDisplay initialDestinationDisplay);
}

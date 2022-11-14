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

import org.onebusaway.gtfs.model.Route;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.Line;

import java.util.Map;


/**
 * Produce a GTFS Route.
 */
public interface RouteProducer {
    /**
     * Produce a GTFS Route for a given NeTEx Line.
     * The route type is mapped from the TransportMode and TransportSubmode of the Line, without taking into account TransportModes and TransportSubmodes overridden at the ServiceJourneyLevel.
     * @deprecated a NeTEx line referencing Service Journeys using different TransportModes cannot be mapped to a single GTFS Route. Use {@link #produceAll(Line)}
     */
    @Deprecated
    Route produce(Line line);

    /**
     * Produce a GTFS routes for each TransportMode used in this NeTEx line.
     *
     */
    Map<AllVehicleModesOfTransportEnumeration, Route> produceAll(Line netexLine);
}

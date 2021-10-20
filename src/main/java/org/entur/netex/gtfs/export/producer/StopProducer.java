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

import org.onebusaway.gtfs.model.Stop;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;


/**
 * Produce a GTFS Stop.
 */
public interface StopProducer {

    int WHEELCHAIR_BOARDING_TRUE = 1;
    int WHEELCHAIR_BOARDING_FALSE = 2;

    /**
     * Produce a GTFS Stop from a NeTEx StopPlace
     * @param stopPlace a NeTEx StopPlace
     * @return a GTFS stop
     */
    Stop produceStopFromStopPlace(StopPlace stopPlace);

    /**
     * Produce a GTFS Stop from a NeTEx Quay
     * @param quay a NeTEx Quay
     * @return a GTFS stop
     */
    Stop produceStopFromQuay(Quay quay);
}

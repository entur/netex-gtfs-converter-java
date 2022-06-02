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

import org.onebusaway.gtfs.model.Transfer;
import org.rutebanken.netex.model.ServiceJourneyInterchange;


/**
 * Produce a GTFS Transfer
 */
public interface TransferProducer {

    int TRANSFER_RECOMMENDED = 0;
    int TRANSFER_TIMED = 1;
    int TRANSFER_MINIMAL = 2;
    int TRANSFER_NOT_ALLOWED = 3;
    int TRANSFER_STAY_SEATED = 4;

    /**
     * Produce a GTFS Transfer from a NeTEx Service Journey Interchange.
     * @param serviceJourneyInterchange the NeTEx Service Journey Interchange.
     * @return the GTFS Transfer.
     */
    Transfer produce(ServiceJourneyInterchange serviceJourneyInterchange);
}

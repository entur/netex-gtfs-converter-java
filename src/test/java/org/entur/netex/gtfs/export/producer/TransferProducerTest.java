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
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.mock.TestGtfsRepository;
import org.entur.netex.gtfs.export.mock.TestNetexDatasetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.Transfer;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.VehicleJourneyRefStructure;

class TransferProducerTest {


    private static final String FROM_TRIP_ID = "";

    @Test
    void testTransferProducer() {

        NetexDatasetRepository netexDatasetRepository = new TestNetexDatasetRepository();
        GtfsDatasetRepository gtfsDatasetRepository = new TestGtfsRepository();

        TransferProducer transferProducer = new DefaultTransferProducer(netexDatasetRepository, gtfsDatasetRepository);

        ServiceJourneyInterchange serviceJourneyInterchange = new ServiceJourneyInterchange();
        VehicleJourneyRefStructure fromJourneyRef = new VehicleJourneyRefStructure();
        serviceJourneyInterchange.setFromJourneyRef(fromJourneyRef);
        VehicleJourneyRefStructure toJourneyRef = new VehicleJourneyRefStructure();
        serviceJourneyInterchange.setToJourneyRef(toJourneyRef);
        ScheduledStopPointRefStructure fromPointRef = new ScheduledStopPointRefStructure();
        serviceJourneyInterchange.setFromPointRef(fromPointRef);
        ScheduledStopPointRefStructure toPointRef = new ScheduledStopPointRefStructure();
        serviceJourneyInterchange.setToPointRef(toPointRef);

        Transfer transfer = transferProducer.produce(serviceJourneyInterchange);


        Assertions.assertNotNull(transfer);
        Assertions.assertNotNull(transfer.getId());
        Assertions.assertNotNull(FROM_TRIP_ID, transfer.getFromTrip().getId().getId());

    }
}

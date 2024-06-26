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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Optional;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.VehicleJourneyRefStructure;

class TransferProducerTest {

  private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();

  private static final String TEST_SERVICE_JOURNEY_FROM_ID =
    "ENT:ServiceJourney:1";
  private static final String TEST_SERVICE_JOURNEY_TO_ID =
    "ENT:ServiceJourney:2";
  private static final String TEST_SCHEDULED_STOP_POINT_FROM_ID =
    "ENT:ScheduledStopPoint:1";
  private static final String TEST_SCHEDULED_STOP_POINT_TO_ID =
    "ENT:ScheduledStopPoint:2";
  private static final String TEST_QUAY_FROM_ID = "ENT:Quay:1";
  private static final String TEST_QUAY_TO_ID = "ENT:Quay:2";

  private NetexDatasetRepository netexDatasetRepository;
  private GtfsDatasetRepository gtfsDatasetRepository;
  private ServiceJourneyInterchange serviceJourneyInterchange;
  private TransferProducer transferProducer;

  @BeforeEach
  void setUp() {
    netexDatasetRepository = getNetexDatasetRepository();
    gtfsDatasetRepository = getGtfsDatasetRepository();

    serviceJourneyInterchange =
      createTestServiceJourneyInterchange(
        TEST_SERVICE_JOURNEY_FROM_ID,
        TEST_SERVICE_JOURNEY_TO_ID,
        TEST_SCHEDULED_STOP_POINT_FROM_ID,
        TEST_SCHEDULED_STOP_POINT_TO_ID
      );

    transferProducer =
      new DefaultTransferProducer(
        netexDatasetRepository,
        gtfsDatasetRepository
      );
  }

  @Test
  void testTransferProducer() {
    Transfer transfer = transferProducer.produce(serviceJourneyInterchange);

    assertTransferFromTo(transfer);
    assertEquals(
      TransferProducer.TRANSFER_RECOMMENDED,
      transfer.getTransferType()
    );
    Assertions.assertFalse(transfer.isMinTransferTimeSet());
  }

  @Test
  void testTransferProducerWithInvalidTransfer() {
    serviceJourneyInterchange.setFromJourneyRef(
      new VehicleJourneyRefStructure().withRef("XXX")
    );
    Transfer transfer = transferProducer.produce(serviceJourneyInterchange);
    assertNull(transfer);
  }

  @Test
  void testTransferProducerWithGuaranteedTransfer() {
    serviceJourneyInterchange.setGuaranteed(true);
    Transfer transfer = transferProducer.produce(serviceJourneyInterchange);

    assertTransferFromTo(transfer);
    assertEquals(TransferProducer.TRANSFER_TIMED, transfer.getTransferType());
    Assertions.assertFalse(transfer.isMinTransferTimeSet());
  }

  @Test
  void testTransferProducerWithDisallowedTransfer() {
    serviceJourneyInterchange.setPriority(BigInteger.valueOf(-1));
    Transfer transfer = transferProducer.produce(serviceJourneyInterchange);

    assertTransferFromTo(transfer);
    assertEquals(
      TransferProducer.TRANSFER_NOT_ALLOWED,
      transfer.getTransferType()
    );
    Assertions.assertFalse(transfer.isMinTransferTimeSet());
  }

  @Test
  void testTransferProducerWithMinimumTimeTransfer() {
    Duration minimumTransferDuration = Duration.ofMinutes(5);
    serviceJourneyInterchange.setMinimumTransferTime(minimumTransferDuration);

    Transfer transfer = transferProducer.produce(serviceJourneyInterchange);

    assertTransferFromTo(transfer);
    assertEquals(TransferProducer.TRANSFER_MINIMAL, transfer.getTransferType());
    Assertions.assertTrue(transfer.isMinTransferTimeSet());
    assertEquals(
      minimumTransferDuration.getSeconds(),
      transfer.getMinTransferTime()
    );
  }

  @Test
  void testTransferProducerWithStaySeatedTransfer() {
    serviceJourneyInterchange.setStaySeated(true);
    serviceJourneyInterchange.setGuaranteed(true);

    TransferProducer staySeatedTransferProducer = new DefaultTransferProducer(
      netexDatasetRepository,
      gtfsDatasetRepository,
      true
    );
    Transfer transfer = staySeatedTransferProducer.produce(
      serviceJourneyInterchange
    );

    assertTransferFromTo(transfer);
    assertEquals(
      TransferProducer.TRANSFER_STAY_SEATED,
      transfer.getTransferType()
    );
    Assertions.assertFalse(transfer.isMinTransferTimeSet());
  }

  private void assertTransferFromTo(Transfer transfer) {
    Assertions.assertNotNull(transfer);
    Assertions.assertNotNull(transfer.getId());
    Assertions.assertNotNull(transfer.getFromTrip());
    assertEquals(
      TEST_SERVICE_JOURNEY_FROM_ID,
      transfer.getFromTrip().getId().getId()
    );
    Assertions.assertNotNull(transfer.getToTrip());
    assertEquals(
      TEST_SERVICE_JOURNEY_TO_ID,
      transfer.getToTrip().getId().getId()
    );
    Assertions.assertNotNull(transfer.getFromStop());
    assertEquals(TEST_QUAY_FROM_ID, transfer.getFromStop().getId().getId());
    Assertions.assertNotNull(transfer.getToStop());
    assertEquals(TEST_QUAY_TO_ID, transfer.getToStop().getId().getId());
  }

  private GtfsDatasetRepository getGtfsDatasetRepository() {
    Trip fromTrip = new Trip();
    fromTrip.setId(new AgencyAndId("DEFAULT", TEST_SERVICE_JOURNEY_FROM_ID));
    Trip toTrip = new Trip();
    toTrip.setId(new AgencyAndId("DEFAULT", TEST_SERVICE_JOURNEY_TO_ID));
    Stop fromStop = new Stop();
    fromStop.setId(new AgencyAndId("DEFAULT", TEST_QUAY_FROM_ID));
    Stop toStop = new Stop();
    toStop.setId(new AgencyAndId("DEFAULT", TEST_QUAY_TO_ID));

    GtfsDatasetRepository gtfsDatasetRepository = mock(
      GtfsDatasetRepository.class
    );
    when(gtfsDatasetRepository.findTripById(TEST_SERVICE_JOURNEY_FROM_ID))
      .thenReturn(Optional.of(fromTrip));
    when(gtfsDatasetRepository.findTripById(TEST_SERVICE_JOURNEY_TO_ID))
      .thenReturn(Optional.of(toTrip));
    when(gtfsDatasetRepository.getStopById(TEST_QUAY_FROM_ID))
      .thenReturn(fromStop);
    when(gtfsDatasetRepository.getStopById(TEST_QUAY_TO_ID)).thenReturn(toStop);
    return gtfsDatasetRepository;
  }

  private NetexDatasetRepository getNetexDatasetRepository() {
    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(
      netexDatasetRepository.getQuayIdByScheduledStopPointId(
        TEST_SCHEDULED_STOP_POINT_FROM_ID
      )
    )
      .thenReturn(TEST_QUAY_FROM_ID);
    when(
      netexDatasetRepository.getQuayIdByScheduledStopPointId(
        TEST_SCHEDULED_STOP_POINT_TO_ID
      )
    )
      .thenReturn(TEST_QUAY_TO_ID);
    return netexDatasetRepository;
  }

  private ServiceJourneyInterchange createTestServiceJourneyInterchange(
    String fromServiceJourneyId,
    String toServiceJourneyId,
    String fromScheduledStopPointId,
    String toScheduledStopPointId
  ) {
    ServiceJourneyInterchange serviceJourneyInterchange =
      new ServiceJourneyInterchange();
    VehicleJourneyRefStructure fromJourneyRef =
      NETEX_FACTORY.createVehicleJourneyRefStructure();
    fromJourneyRef.setRef(fromServiceJourneyId);
    serviceJourneyInterchange.setFromJourneyRef(fromJourneyRef);

    VehicleJourneyRefStructure toJourneyRef =
      NETEX_FACTORY.createVehicleJourneyRefStructure();
    toJourneyRef.setRef(toServiceJourneyId);
    serviceJourneyInterchange.setToJourneyRef(toJourneyRef);

    ScheduledStopPointRefStructure fromPointRef =
      NETEX_FACTORY.createScheduledStopPointRefStructure();
    fromPointRef.setRef(fromScheduledStopPointId);
    serviceJourneyInterchange.setFromPointRef(fromPointRef);

    ScheduledStopPointRefStructure toPointRef =
      NETEX_FACTORY.createScheduledStopPointRefStructure();
    toPointRef.setRef(toScheduledStopPointId);
    serviceJourneyInterchange.setToPointRef(toPointRef);
    return serviceJourneyInterchange;
  }
}

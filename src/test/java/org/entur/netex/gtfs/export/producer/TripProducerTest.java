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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBElement;
import java.time.LocalDateTime;
import java.util.Set;
import org.entur.netex.gtfs.export.repository.DefaultGtfsRepository;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.*;

class TripProducerTest {

  private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();

  private static final String TEST_SERVICE_JOURNEY_ID = "ServiceJourney-Id";
  private static final String FRONT_TEXT = "Front Text";

  private static final String TEST_DAY_TYPE_ID = "ENT:DayType:1";
  private static final String CODESPACE = "ENT";
  private static final String TEST_DATED_SERVICE_JOURNEY_ID =
    "ENT:DatedServiceJourney:1";
  private static final String TEST_OPERATING_DAY_ID = "ENT:OperatingDay:1";
  private static final LocalDateTime TEST_DATE = LocalDateTime.of(
    2024,
    4,
    3,
    12,
    0,
    0
  );
  private DefaultGtfsRepository gtfsDatasetRepository;
  private DefaultGtfsServiceRepository gtfsServiceRepository;
  private NetexDatasetRepository netexDatasetRepository;

  @BeforeEach
  void setup() {
    netexDatasetRepository = mock(NetexDatasetRepository.class);
    gtfsDatasetRepository = new DefaultGtfsRepository();
    gtfsServiceRepository =
      new DefaultGtfsServiceRepository(CODESPACE, netexDatasetRepository);
  }

  @Test
  void testTripProducer() {
    ServiceJourney serviceJourney = createTestServiceJourney(
      TEST_SERVICE_JOURNEY_ID,
      TEST_DAY_TYPE_ID
    );
    DayType dayType = createTestDayType(TEST_DAY_TYPE_ID);
    DayTypeAssignment dayTypeAssignment = createTestDayTypeAssignment(
      dayType,
      TEST_DATE
    );

    when(netexDatasetRepository.getDayTypeById(TEST_DAY_TYPE_ID))
      .thenReturn(dayType);
    when(netexDatasetRepository.getDayTypeAssignmentsByDayType(dayType))
      .thenReturn(Set.of(dayTypeAssignment));

    Trip trip = produceTrip(
      netexDatasetRepository,
      gtfsDatasetRepository,
      gtfsServiceRepository,
      serviceJourney
    );

    assertNotNull(trip);
    assertNotNull(trip.getId());
    assertEquals(TEST_SERVICE_JOURNEY_ID, trip.getId().getId());
    assertEquals(TripProducer.GTFS_DIRECTION_INBOUND, trip.getDirectionId());
    assertEquals(FRONT_TEXT, trip.getTripHeadsign());

    assertEquals(TEST_DAY_TYPE_ID, trip.getServiceId().getId());
  }

  @Test
  void testTripProducerWithoutDayTypeAssignment() {
    ServiceJourney serviceJourney = createTestServiceJourney(
      TEST_SERVICE_JOURNEY_ID,
      TEST_DAY_TYPE_ID
    );
    DayType dayType = createTestDayType(TEST_DAY_TYPE_ID);

    when(netexDatasetRepository.getDayTypeById(TEST_DAY_TYPE_ID))
      .thenReturn(dayType);

    Trip trip = produceTrip(
      netexDatasetRepository,
      gtfsDatasetRepository,
      gtfsServiceRepository,
      serviceJourney
    );
    Assertions.assertNull(trip);
  }

  @Test
  void testTripProducerWithDatedServiceJourney() {
    ServiceJourney serviceJourney = createTestServiceJourney(
      TEST_SERVICE_JOURNEY_ID,
      null
    );

    OperatingDay operatingDay = createTestOperatingDay(
      TEST_OPERATING_DAY_ID,
      TEST_DATE
    );
    DatedServiceJourney datedServiceJourney = createTestDatedServiceJourney(
      TEST_SERVICE_JOURNEY_ID,
      TEST_OPERATING_DAY_ID
    );

    when(
      netexDatasetRepository.getDatedServiceJourneysByServiceJourneyId(
        TEST_SERVICE_JOURNEY_ID
      )
    )
      .thenReturn(Set.of(datedServiceJourney));
    when(netexDatasetRepository.getOperatingDayById(TEST_OPERATING_DAY_ID))
      .thenReturn(operatingDay);

    Trip trip = produceTrip(
      netexDatasetRepository,
      gtfsDatasetRepository,
      gtfsServiceRepository,
      serviceJourney
    );

    assertNotNull(trip);
    assertNotNull(trip.getId());
    assertEquals(TEST_SERVICE_JOURNEY_ID, trip.getId().getId());
    assertEquals(TripProducer.GTFS_DIRECTION_INBOUND, trip.getDirectionId());
    assertEquals(FRONT_TEXT, trip.getTripHeadsign());

    assertEquals(TEST_OPERATING_DAY_ID, trip.getServiceId().getId());
  }

  private static Trip produceTrip(
    NetexDatasetRepository netexDatasetRepository,
    GtfsDatasetRepository gtfsDatasetRepository,
    GtfsServiceRepository gtfsServiceRepository,
    ServiceJourney serviceJourney
  ) {
    Route netexRoute = new Route();
    netexRoute.setDirectionType(DirectionTypeEnumeration.INBOUND);
    org.onebusaway.gtfs.model.Route gtfsRoute =
      new org.onebusaway.gtfs.model.Route();
    AgencyAndId shapeId = new AgencyAndId();
    DestinationDisplay initialDestinationDisplay = new DestinationDisplay();
    MultilingualString frontText = new MultilingualString();
    frontText.setValue(FRONT_TEXT);
    initialDestinationDisplay.setFrontText(frontText);

    TripProducer tripProducer = new DefaultTripProducer(
      netexDatasetRepository,
      gtfsDatasetRepository,
      gtfsServiceRepository
    );

    return tripProducer.produce(
      serviceJourney,
      netexRoute,
      gtfsRoute,
      shapeId,
      initialDestinationDisplay
    );
  }

  private ServiceJourney createTestServiceJourney(
    String serviceJourneyId,
    String dayTypeId
  ) {
    ServiceJourney serviceJourney = new ServiceJourney();
    serviceJourney.setId(serviceJourneyId);

    if (dayTypeId != null) {
      DayTypeRefs_RelStructure dayTypeStruct =
        NETEX_FACTORY.createDayTypeRefs_RelStructure();
      serviceJourney.setDayTypes(dayTypeStruct);
      DayTypeRefStructure dayTypeRefStruct =
        NETEX_FACTORY.createDayTypeRefStructure();
      dayTypeRefStruct.setRef(dayTypeId);
      serviceJourney
        .getDayTypes()
        .getDayTypeRef()
        .add(NETEX_FACTORY.createDayTypeRef(dayTypeRefStruct));
    }
    return serviceJourney;
  }

  private DatedServiceJourney createTestDatedServiceJourney(
    String serviceJourneyId,
    String operatingDayId
  ) {
    DatedServiceJourney datedServiceJourney =
      NETEX_FACTORY.createDatedServiceJourney();
    datedServiceJourney.setId(TEST_DATED_SERVICE_JOURNEY_ID);

    ServiceJourneyRefStructure serviceJourneyRefStructure =
      NETEX_FACTORY.createServiceJourneyRefStructure();
    serviceJourneyRefStructure.setRef(serviceJourneyId);
    JAXBElement<ServiceJourneyRefStructure> serviceJourneyRef =
      NETEX_FACTORY.createServiceJourneyRef(serviceJourneyRefStructure);
    serviceJourneyRef.setValue(serviceJourneyRefStructure);
    datedServiceJourney.getJourneyRef().add(serviceJourneyRef);

    OperatingDayRefStructure operatingDayRef =
      NETEX_FACTORY.createOperatingDayRefStructure();
    operatingDayRef.setRef(operatingDayId);
    datedServiceJourney.setOperatingDayRef(operatingDayRef);

    return datedServiceJourney;
  }

  private static DayType createTestDayType(String dayTypeId) {
    return NETEX_FACTORY.createDayType().withId(dayTypeId);
  }

  private static DayTypeAssignment createTestDayTypeAssignment(
    DayType dayType,
    LocalDateTime date
  ) {
    DayTypeRefStructure dayTypeRefStructure = NETEX_FACTORY
      .createDayTypeRefStructure()
      .withRef(dayType.getId());
    JAXBElement<? extends DayTypeRefStructure> dayTypeRef =
      NETEX_FACTORY.createDayTypeRef(dayTypeRefStructure);
    return NETEX_FACTORY
      .createDayTypeAssignment()
      .withDayTypeRef(dayTypeRef)
      .withDate(date);
  }

  private static OperatingDay createTestOperatingDay(
    String operatingDayId,
    LocalDateTime date
  ) {
    return NETEX_FACTORY
      .createOperatingDay()
      .withId(operatingDayId)
      .withCalendarDate(date);
  }
}

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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.entur.netex.gtfs.export.exception.GtfsExportException;
import org.entur.netex.gtfs.export.model.GtfsRouteType;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex2.model.AllPublicTransportModesEnumeration;
import org.entur.netex2.model.BusSubmodeEnumeration;
import org.entur.netex2.model.GroupOfLinesRefStructure;
import org.entur.netex2.model.Line;
import org.entur.netex2.model.MultilingualString;
import org.entur.netex2.model.PublicCodeStructure;
import org.entur.netex2.model.TransportSubmodeStructure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Route;

class RouteProducerTest {

  private static final String TEST_LINE_ID = "ENT:Line:1";
  private static final String LINE_NAME = "Line-Name";
  private static final String LINE_SHORT_NAME = "Line-Short-Name";
  private static final String LINE_PUBLIC_CODE = "Line-Public-Code";
  private static final String TEST_AUTHORITY_ID = "ENT:Authority:1";
  private static final String TEST_NETWORK_ID = "ENT:Network:1";

  @Test
  void testRouteProducer() {
    Line line = createTestLine();

    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(netexDatasetRepository.getAuthorityIdForLine(line))
      .thenReturn(TEST_AUTHORITY_ID);

    GtfsDatasetRepository gtfsDatasetRepository = mock(
      GtfsDatasetRepository.class
    );
    when(gtfsDatasetRepository.getAgencyById(TEST_AUTHORITY_ID))
      .thenReturn(new Agency());

    RouteProducer routeProducer = new DefaultRouteProducer(
      netexDatasetRepository,
      gtfsDatasetRepository
    );
    Route route = routeProducer.produce(line);

    Assertions.assertNotNull(route);
    Assertions.assertNotNull(route.getId());
    Assertions.assertEquals(TEST_LINE_ID, route.getId().getId());
    Assertions.assertEquals(
      LINE_PUBLIC_CODE,
      route.getShortName(),
      "The GTFS route short name should be the NeTEx Line public code"
    );
    Assertions.assertEquals(
      LINE_NAME,
      route.getLongName(),
      "The GTFS route long name should be the NeTEx Line name"
    );

    Assertions.assertEquals(
      GtfsRouteType.LOCAL_BUS_SERVICE.getValue(),
      route.getType()
    );
  }

  @Test
  void testRouteProducerWithoutShortName() {
    Line line = createTestLine();
    line.setShortName(null);

    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(netexDatasetRepository.getAuthorityIdForLine(line))
      .thenReturn(TEST_AUTHORITY_ID);

    GtfsDatasetRepository gtfsDatasetRepository = mock(
      GtfsDatasetRepository.class
    );
    Agency agency = new Agency();
    agency.setId(TEST_AUTHORITY_ID);
    when(gtfsDatasetRepository.getAgencyById(TEST_AUTHORITY_ID))
      .thenReturn(agency);

    RouteProducer routeProducer = new DefaultRouteProducer(
      netexDatasetRepository,
      gtfsDatasetRepository
    );

    Route route = routeProducer.produce(line);

    Assertions.assertEquals(
      LINE_PUBLIC_CODE,
      route.getShortName(),
      "The GTFS route short name should be the NeTEx Line public code"
    );
    Assertions.assertEquals(
      LINE_NAME,
      route.getLongName(),
      "The GTFS route long name should be the NeTEx Line name"
    );
  }

  @Test
  void testRouteProducerWithoutPublicCode() {
    Line line = createTestLine();
    line.setPublicCode(null);

    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(netexDatasetRepository.getAuthorityIdForLine(line))
      .thenReturn(TEST_AUTHORITY_ID);

    GtfsDatasetRepository gtfsDatasetRepository = mock(
      GtfsDatasetRepository.class
    );
    Agency agency = new Agency();
    agency.setId(TEST_AUTHORITY_ID);
    when(gtfsDatasetRepository.getAgencyById(TEST_AUTHORITY_ID))
      .thenReturn(agency);

    RouteProducer routeProducer = new DefaultRouteProducer(
      netexDatasetRepository,
      gtfsDatasetRepository
    );

    Route route = routeProducer.produce(line);

    Assertions.assertEquals(
      LINE_SHORT_NAME,
      route.getShortName(),
      "The GTFS route short name should be the NeTEx Line short name if the NeTEx Line public code is missing"
    );
    Assertions.assertEquals(
      LINE_NAME,
      route.getLongName(),
      "The GTFS route long name should be the NeTEx Line name"
    );
  }

  @Test
  void testRouteProducerWithoutName() {
    Line line = createTestLine();
    line.setName(null);

    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(netexDatasetRepository.getAuthorityIdForLine(line))
      .thenReturn(TEST_AUTHORITY_ID);

    GtfsDatasetRepository gtfsDatasetRepository = mock(
      GtfsDatasetRepository.class
    );
    Agency agency = new Agency();
    agency.setId(TEST_AUTHORITY_ID);
    when(gtfsDatasetRepository.getAgencyById(TEST_AUTHORITY_ID))
      .thenReturn(agency);

    RouteProducer routeProducer = new DefaultRouteProducer(
      netexDatasetRepository,
      gtfsDatasetRepository
    );

    Route route = routeProducer.produce(line);

    Assertions.assertEquals(
      LINE_PUBLIC_CODE,
      route.getShortName(),
      "The GTFS route short name should be the NeTEx Line public code"
    );
    Assertions.assertEquals(
      LINE_SHORT_NAME,
      route.getLongName(),
      "The GTFS route long name should be the NeTEx Line short name if the NeTEx Line name is missing"
    );
  }

  @Test
  void testRouteProducerWithOnlyShortName() {
    Line line = createTestLine();
    line.setPublicCode(null);
    line.setName(null);

    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(netexDatasetRepository.getAuthorityIdForLine(line))
      .thenReturn(TEST_AUTHORITY_ID);

    GtfsDatasetRepository gtfsDatasetRepository = mock(
      GtfsDatasetRepository.class
    );
    Agency agency = new Agency();
    agency.setId(TEST_AUTHORITY_ID);
    when(gtfsDatasetRepository.getAgencyById(TEST_AUTHORITY_ID))
      .thenReturn(agency);

    RouteProducer routeProducer = new DefaultRouteProducer(
      netexDatasetRepository,
      gtfsDatasetRepository
    );

    Route route = routeProducer.produce(line);

    Assertions.assertEquals(
      LINE_SHORT_NAME,
      route.getShortName(),
      "The GTFS route short name should be the NeTEx Line short name if the NeTEx Line public code is missing"
    );
    Assertions.assertNull(
      route.getLongName(),
      "The GTFS route long name is not set when it would be identical to the short name"
    );
  }

  @Test
  void testRouteProducerWithIdenticalPublicCodeAndName() {
    Line line = createTestLine();
    line.setPublicCode(new PublicCodeStructure().withValue(LINE_NAME));

    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(netexDatasetRepository.getAuthorityIdForLine(line))
      .thenReturn(TEST_AUTHORITY_ID);

    GtfsDatasetRepository gtfsDatasetRepository = mock(
      GtfsDatasetRepository.class
    );
    Agency agency = new Agency();
    agency.setId(TEST_AUTHORITY_ID);
    when(gtfsDatasetRepository.getAgencyById(TEST_AUTHORITY_ID))
      .thenReturn(agency);

    RouteProducer routeProducer = new DefaultRouteProducer(
      netexDatasetRepository,
      gtfsDatasetRepository
    );

    Route route = routeProducer.produce(line);

    Assertions.assertEquals(
      LINE_NAME,
      route.getShortName(),
      "The GTFS route short name should be the NeTEx Line public code"
    );
    Assertions.assertNull(
      route.getLongName(),
      "The GTFS route long name is not set when NeTEx public code and name are identical"
    );
  }

  @Test
  void testRouteProducerWithoutAnyName() {
    Line line = createTestLine();
    line.setPublicCode(null);
    line.setShortName(null);
    line.setName(null);

    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(netexDatasetRepository.getAuthorityIdForLine(line))
      .thenReturn(TEST_AUTHORITY_ID);

    GtfsDatasetRepository gtfsDatasetRepository = mock(
      GtfsDatasetRepository.class
    );
    Agency agency = new Agency();
    agency.setId(TEST_AUTHORITY_ID);
    when(gtfsDatasetRepository.getAgencyById(TEST_AUTHORITY_ID))
      .thenReturn(agency);

    RouteProducer routeProducer = new DefaultRouteProducer(
      netexDatasetRepository,
      gtfsDatasetRepository
    );

    Assertions.assertThrows(
      GtfsExportException.class,
      () -> routeProducer.produce(line),
      "A line without public code, short name and name cannot be exported"
    );
  }

  private Line createTestLine() {
    Line line = new Line();
    line.setId(TEST_LINE_ID);
    line.setPublicCode(new PublicCodeStructure().withValue(LINE_PUBLIC_CODE));

    MultilingualString lineName = new MultilingualString()
      .withContent(LINE_NAME);
    line.setName(lineName);

    MultilingualString lineShortName = new MultilingualString()
      .withContent(LINE_SHORT_NAME);
    line.setShortName(lineShortName);

    line.setTransportMode(AllPublicTransportModesEnumeration.BUS);
    TransportSubmodeStructure transportSubmode =
      new TransportSubmodeStructure();
    transportSubmode.setBusSubmode(BusSubmodeEnumeration.LOCAL_BUS);
    line.setTransportSubmode(transportSubmode);

    GroupOfLinesRefStructure groupOfLineRef = new GroupOfLinesRefStructure();
    groupOfLineRef.setRef(TEST_NETWORK_ID);
    line.setRepresentedByGroupRef(groupOfLineRef);

    return line;
  }
}

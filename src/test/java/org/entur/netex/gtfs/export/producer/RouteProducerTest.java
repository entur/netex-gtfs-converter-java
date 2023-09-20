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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.entur.netex.gtfs.export.model.GtfsRouteType;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Route;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.BusSubmodeEnumeration;
import org.rutebanken.netex.model.GroupOfLinesRefStructure;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.TransportSubmodeStructure;

class RouteProducerTest {

  private static final String TEST_LINE_ID = "ENT:Line:1";
  private static final String LINE_NAME = "Line-Name";
  private static final String LINE_SHORT_NAME = "Line-Short-Name";
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
      line.getPublicCode(),
      route.getShortName(),
      "The GTFS route short name should be the NeTEx Line public code"
    );
    Assertions.assertEquals(
      line.getShortName().getValue(),
      route.getLongName(),
      "The GTFS route long name should be the NeTEx Line short name"
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
      line.getPublicCode(),
      route.getShortName(),
      "The GTFS route short name should be the NeTEx Line public code"
    );
    Assertions.assertEquals(
      line.getName().getValue(),
      route.getLongName(),
      "The GTFS route long name should be the NeTEx Line name if the NeTEx Line short name is missing"
    );
  }

  @Test
  void testRouteProducerWithIdenticalPublicCodeAndShortName() {
    Line line = createTestLine();
    line.setPublicCode(LINE_SHORT_NAME);

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
      line.getPublicCode(),
      route.getShortName(),
      "The GTFS route short name should be the NeTEx Line public code"
    );
    Assertions.assertNull(
      route.getLongName(),
      "The GTFS route long name is not set when NeTEx public code and short name are identical"
    );
  }

  private Line createTestLine() {
    Line line = new Line();
    line.setId(TEST_LINE_ID);

    MultilingualString lineName = new MultilingualString();
    lineName.setValue(LINE_NAME);
    line.setName(lineName);

    MultilingualString lineShortName = new MultilingualString();
    lineShortName.setValue(LINE_SHORT_NAME);
    line.setShortName(lineShortName);

    line.setTransportMode(AllVehicleModesOfTransportEnumeration.BUS);
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

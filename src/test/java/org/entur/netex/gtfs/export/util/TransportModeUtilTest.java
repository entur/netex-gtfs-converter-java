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

package org.entur.netex.gtfs.export.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.entur.netex.gtfs.export.model.GtfsRouteType;
import org.entur.netex2.model.AllPublicTransportModesEnumeration;
import org.entur.netex2.model.BusSubmodeEnumeration;
import org.entur.netex2.model.Line;
import org.entur.netex2.model.TransportSubmodeStructure;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TransportModeUtilTest {

  @ParameterizedTest
  @CsvSource(
    {
      "SCHOOL_BUS, SCHOOL_BUS",
      "SCHOOL_AND_PUBLIC_SERVICE_BUS, SCHOOL_AND_PUBLIC_SERVICE_BUS",
      "LOCAL_BUS, LOCAL_BUS_SERVICE",
    }
  )
  void testBusSubmodeMapping(
    BusSubmodeEnumeration busSubmode,
    GtfsRouteType expectedRouteType
  ) {
    Line line = new Line();
    line.setTransportMode(AllPublicTransportModesEnumeration.BUS);
    TransportSubmodeStructure transportSubmode =
      new TransportSubmodeStructure();
    transportSubmode.setBusSubmode(busSubmode);
    line.setTransportSubmode(transportSubmode);

    assertEquals(
      expectedRouteType.getValue(),
      TransportModeUtil.getGtfsExtendedRouteType(line)
    );
  }
}

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

import net.opengis.gml._3.AbstractRingPropertyType;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LinearRingType;
import net.opengis.gml._3.PolygonType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.Location;
import org.rutebanken.netex.model.FlexibleArea;
import org.rutebanken.netex.model.FlexibleStopPlace;
import org.rutebanken.netex.model.FlexibleStopPlace_VersionStructure;
import org.rutebanken.netex.model.ObjectFactory;

import java.util.ArrayList;
import java.util.List;

class LocationProducerTest {

    private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();
    private static final net.opengis.gml._3.ObjectFactory openGisObjectFactory = new net.opengis.gml._3.ObjectFactory();
    private static final String TEST_FLEXIBLE_STOP_PLACE_ID = "ENT:FlexibleArea:1";


    @Test
    void testLocationProducer() {

        FlexibleArea flexibleArea = NETEX_FACTORY.createFlexibleArea();
        PolygonType gmlPolygon = createTestGmlPolygon();
        flexibleArea.withPolygon(gmlPolygon);

        FlexibleStopPlace flexibleStopPlace = NETEX_FACTORY.createFlexibleStopPlace();
        flexibleStopPlace.setId(TEST_FLEXIBLE_STOP_PLACE_ID);

        FlexibleStopPlace_VersionStructure.Areas areas = NETEX_FACTORY.createFlexibleStopPlace_VersionStructureAreas();
        areas.getFlexibleAreaOrFlexibleAreaRefOrHailAndRideArea().add(flexibleArea);
        flexibleStopPlace.setAreas(areas);

        LocationProducer locationProducer = new DefaultLocationProducer();
        Location location = locationProducer.produceLocation(flexibleStopPlace);
        Assertions.assertNotNull(location);
        Assertions.assertEquals(TEST_FLEXIBLE_STOP_PLACE_ID, location.getId().getId());
    }

    private PolygonType createTestGmlPolygon() {
        List<Double> values = new ArrayList<>();
        values.add(9.8468);
        values.add(59.2649);
        values.add(9.8456);
        values.add(59.2654);
        values.add(9.8457);
        values.add(59.2655);
        values.add(values.get(0));
        values.add(values.get(1));

        DirectPositionListType positionList = new DirectPositionListType().withValue(values);

        LinearRingType linearRing = new LinearRingType()
                .withPosList(positionList);

        return new PolygonType()
                .withId("P1")
                .withExterior(new AbstractRingPropertyType()
                        .withAbstractRing(openGisObjectFactory.createLinearRing(linearRing)))
                .withInterior(new AbstractRingPropertyType().withAbstractRing(openGisObjectFactory.createLinearRing(linearRing)));
    }


}

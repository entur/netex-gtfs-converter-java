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

import org.entur.netex.gtfs.export.exception.GtfsExportException;
import org.entur.netex.gtfs.export.repository.DefaultGtfsRepository;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.stop.StopAreaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.Stop;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.StopPlace;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StopProducerTest {

    private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();


    private static final String QUAY_ID = "ENT:Quay:1";
    private static final String STOP_PLACE_ID = "ENT:StopPlace:1";
    private static final double LONGITUDE = 10.0;
    private static final double LATITUDE = 1.0;
    private static final String TEST_STOP_PLACE_NAME = "StopPlace name";

    @Test
    void testStopProducerFromQuay() {

        Quay quay = createTestQuay(QUAY_ID, LONGITUDE, LATITUDE);
        StopPlace stopPlace = createTestStopPlace(STOP_PLACE_ID);

        StopAreaRepository stopAreaRepository = mock(StopAreaRepository.class);
        when(stopAreaRepository.getQuayById(QUAY_ID)).thenReturn(quay);
        when(stopAreaRepository.getStopPlaceByQuayId(QUAY_ID)).thenReturn(stopPlace);

        GtfsDatasetRepository gtfsDatasetRepository = new DefaultGtfsRepository();
        StopProducer stopProducer = new DefaultStopProducer(stopAreaRepository, gtfsDatasetRepository);
        Stop stop = stopProducer.produceStopFromQuay(quay);

        Assertions.assertNotNull(stop);
        Assertions.assertNotNull(stop.getId());
        Assertions.assertEquals(QUAY_ID, stop.getId().getId());
        Assertions.assertEquals(LONGITUDE, stop.getLon());
        Assertions.assertEquals(LATITUDE, stop.getLat());

    }

    @Test
    void testStopProducerFromQuayWithoutName() {
        testStopProducerFromQuayWithName(null);
    }

    @Test
    void testStopProducerFromQuayWithEmptyName() {
        testStopProducerFromQuayWithName(new MultilingualString());
    }

    @Test
    void testStopProducerFromQuayWithBlankName() {
        MultilingualString name = new MultilingualString();
        name.setValue(" ");
        testStopProducerFromQuayWithName(name);
    }

    private void testStopProducerFromQuayWithName(MultilingualString name) {

        Quay quay = createTestQuay(QUAY_ID, LONGITUDE, LATITUDE);
        StopPlace stopPlace = createTestStopPlace(STOP_PLACE_ID);
        stopPlace.setName(name);

        StopAreaRepository stopAreaRepository = mock(StopAreaRepository.class);
        when(stopAreaRepository.getQuayById(QUAY_ID)).thenReturn(quay);
        when(stopAreaRepository.getStopPlaceByQuayId(QUAY_ID)).thenReturn(stopPlace);

        GtfsDatasetRepository gtfsDatasetRepository = new DefaultGtfsRepository();
        StopProducer stopProducer = new DefaultStopProducer(stopAreaRepository, gtfsDatasetRepository);
        Assertions.assertThrows(GtfsExportException.class, () -> stopProducer.produceStopFromQuay(quay));
    }

    private StopPlace createTestStopPlace(String stopPlaceId) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId(stopPlaceId);
        MultilingualString stopPlaceName = NETEX_FACTORY.createMultilingualString();
        stopPlaceName.setValue(TEST_STOP_PLACE_NAME);
        stopPlace.setName(stopPlaceName);

        return stopPlace;
    }

    private Quay createTestQuay(String quayId, double longitude, double latitude) {
        Quay quay = new Quay();
        quay.setId(quayId);
        SimplePoint_VersionStructure centroid = new SimplePoint_VersionStructure();
        LocationStructure location = new LocationStructure();
        location.setLongitude(BigDecimal.valueOf(longitude));
        location.setLatitude(BigDecimal.valueOf(latitude));
        centroid.setLocation(location);
        quay.setCentroid(centroid);

        return quay;
    }
}

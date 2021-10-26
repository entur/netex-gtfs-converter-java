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

import org.entur.netex.gtfs.export.repository.DefaultGtfsRepository;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.mock.TestStopAreaRepository;
import org.entur.netex.gtfs.export.stop.StopAreaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.Stop;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;

import java.math.BigDecimal;

class StopProducerTest {

    private static final String QUAY_ID = "Quay-ID";
    private static final BigDecimal LONGITUDE = BigDecimal.TEN;
    private static final BigDecimal LATITUDE = BigDecimal.ONE;

    @Test
    void testStopProducerFromQuay() {

        StopAreaRepository stopAreaRepository = new TestStopAreaRepository();
        GtfsDatasetRepository gtfsDatasetRepository = new DefaultGtfsRepository();

        StopProducer stopProducer = new DefaultStopProducer(stopAreaRepository, gtfsDatasetRepository);
        Quay quay = new Quay();
        quay.setId(QUAY_ID);
        SimplePoint_VersionStructure centroid = new SimplePoint_VersionStructure();
        LocationStructure location = new LocationStructure();
        location.setLongitude(LONGITUDE);
        location.setLatitude(LATITUDE);
        centroid.setLocation(location);
        quay.setCentroid(centroid);
        Stop stop = stopProducer.produceStopFromQuay(quay);

        Assertions.assertNotNull(stop);
        Assertions.assertNotNull(stop.getId());
        Assertions.assertEquals(QUAY_ID, stop.getId().getId());
        Assertions.assertEquals(LONGITUDE.doubleValue(), stop.getLon());
        Assertions.assertEquals(LATITUDE.doubleValue(), stop.getLat());

    }
}

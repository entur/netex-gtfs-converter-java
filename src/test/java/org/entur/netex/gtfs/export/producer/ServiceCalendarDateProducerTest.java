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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.text.ParseException;
import java.time.LocalDateTime;

class ServiceCalendarDateProducerTest {

    private static final String SERVICE_ID = "Service-ID";

    @Test
    void testServiceCalendarDateProducer() throws ParseException {

        GtfsDatasetRepository gtfsDatasetRepository = new DefaultGtfsRepository();

        ServiceCalendarDateProducer serviceCalendarDateProducer = new DefaultServiceCalendarDateProducer(gtfsDatasetRepository);
        LocalDateTime now = LocalDateTime.of(2021, 10, 15, 0, 0);
        ServiceDate serviceDate = ServiceDate.parseString("20211015");
        ServiceCalendarDate serviceCalendarDate = serviceCalendarDateProducer.produce(SERVICE_ID, now, true);

        Assertions.assertNotNull(serviceCalendarDate);
        Assertions.assertNotNull(serviceCalendarDate.getId());
        Assertions.assertEquals(SERVICE_ID, serviceCalendarDate.getServiceId().getId());
        Assertions.assertEquals(serviceDate, serviceCalendarDate.getDate());
        Assertions.assertEquals(ServiceCalendarDateProducer.SERVICE_ADDED, serviceCalendarDate.getExceptionType());

    }
}

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
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.rutebanken.netex.model.DayOfWeekEnumeration;

import java.util.ArrayList;
import java.util.Collection;

class ServiceCalendarProducerTest {

    private static final String SERVICE_ID = "Service-ID";

    @Test
    void testServiceCalendarProducer() {

        GtfsDatasetRepository gtfsDatasetRepository = new DefaultGtfsRepository();

        ServiceCalendarProducer serviceCalendarProducer = new DefaultServiceCalendarProducer(gtfsDatasetRepository);
        ServiceDate startDate = new ServiceDate();
        ServiceDate endDate = new ServiceDate();
        Collection<DayOfWeekEnumeration> daysOfWeek = new ArrayList<>();
        ServiceCalendar serviceCalendar = serviceCalendarProducer.produce(SERVICE_ID, startDate, endDate, daysOfWeek);

        Assertions.assertNotNull(serviceCalendar);
        Assertions.assertNotNull(serviceCalendar.getId());
        Assertions.assertEquals(SERVICE_ID, serviceCalendar.getServiceId().getId());
        Assertions.assertEquals(startDate, serviceCalendar.getStartDate());
        Assertions.assertEquals(endDate, serviceCalendar.getEndDate());
        Assertions.assertEquals(ServiceCalendarProducer.SERVICE_AVAILABLE, serviceCalendar.getMonday());

    }
}

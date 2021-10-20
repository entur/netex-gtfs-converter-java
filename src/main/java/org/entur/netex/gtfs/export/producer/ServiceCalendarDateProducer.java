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

import org.onebusaway.gtfs.model.ServiceCalendarDate;

import java.time.LocalDateTime;


/**
 * Produce a GTFS Service Calendar Date.
 */
public interface ServiceCalendarDateProducer {

    int SERVICE_ADDED = 1;
    int SERVICE_REMOVED = 2;

    /**
     * Produce a GTFS Service Calendar Date for a given GTFS service.
     * @param serviceId the id of the GTFS service
     * @param date the service date.
     * @param isAvailable true if the service runs on this date.
     * @return a GTFS Service Calendar Date for the given GTFS service.
     */
    ServiceCalendarDate produce(String serviceId, LocalDateTime date, boolean isAvailable);
}

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

import java.time.DayOfWeek;
import java.util.Collection;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

/**
 * Produce a GTFS Service Calendar.
 */
public interface ServiceCalendarProducer {
  int SERVICE_UNAVAILABLE = 0;
  int SERVICE_AVAILABLE = 1;

  /**
   * Produce a GTFS Service Calendar for a given GTFS service.
   * @param serviceId the id of the GTFS service.
   * @param startDate the start date of the calendar period.
   * @param endDate the end date of the calendar period.
   * @param daysOfWeeks the days of week on which the service runs. If empty the service runs every day of the week.
   * @return the GTFS service calendar for this GTFS service.
   */
  ServiceCalendar produce(
    String serviceId,
    ServiceDate startDate,
    ServiceDate endDate,
    Collection<DayOfWeek> daysOfWeeks
  );
}

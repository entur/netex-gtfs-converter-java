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

import java.time.LocalDate;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

/**
 * Utility class for converting NeTEx dates to GTFS service dates.
 */
public final class ServiceDateUtil {

  private ServiceDateUtil() {}

  /**
   * Convert a NeTEx calendar date to a GTFS service date.
   *
   * @param netexDate the NeTEx date
   * @return the GTFS service date for the same calendar day.
   */
  public static ServiceDate toServiceDate(LocalDate netexDate) {
    return new ServiceDate(
      netexDate.getYear(),
      netexDate.getMonthValue(),
      netexDate.getDayOfMonth()
    );
  }
}

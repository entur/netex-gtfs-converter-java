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

import java.time.LocalTime;

/**
 * Utility class for converting NeTEx values to GTFS-compatible values.
 */
public final class GtfsUtil {

  private GtfsUtil() {}

  /**
   * Return the number of seconds since midnight for this local time.
   *
   * @param netexTime a NeTEx time.
   * @return the GTFS time represented by the number of seconds since midnight for this local time.
   */
  public static int toGtfsTime(LocalTime netexTime) {
    return netexTime.toSecondOfDay();
  }

  /**
   * Return the number of seconds since midnight for this local time plus the number of seconds (positive or negative) corresponding to the day offset.
   * @param netexTime a NeTEx time.
   * @param dayOffset a NeTEx day offset.
   * @return the GTFS time represented by the number of seconds since midnight for this local time plus the number of seconds (positive or negative) corresponding to the day offset.
   */
  public static int toGtfsTimeWithDayOffset(
    LocalTime netexTime,
    int dayOffset
  ) {
    return toGtfsTime(netexTime) + dayOffset * 60 * 60 * 24;
  }
}

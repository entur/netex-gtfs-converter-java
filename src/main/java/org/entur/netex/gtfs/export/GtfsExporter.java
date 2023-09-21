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

package org.entur.netex.gtfs.export;

import java.io.InputStream;

/**
 * Entry point class for generating a GTFS dataset from a NeTEx dataset
 */
public interface GtfsExporter {
  /**
   * Convert a Netex timetable dataset into a GTFS dataset.
   * @param netexTimetableDataset a ZIP archive containing a NeTEx timetable dataset.
   * @return a ZIP archive containing a GTFS dataset.
   */
  InputStream convertTimetablesToGtfs(InputStream netexTimetableDataset);

  /**
   * Export the stop area repository to GTFS. No timetable data is exported.
   * @return a GTFS ZIP archive containing all stops.
   */
  InputStream convertStopsToGtfs();
}

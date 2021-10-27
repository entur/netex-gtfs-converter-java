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
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package org.entur.netex.gtfs.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.entur.netex.gtfs.export.stop.DefaultStopAreaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Coarse-grained integration test that verifies that the individual GTFS file entries are generated.
 */
class GtfsExportTest {

    @Test
    void testExport() throws IOException {

        DefaultStopAreaRepository defaultStopAreaRepository = new DefaultStopAreaRepository();
        defaultStopAreaRepository.loadStopAreas(getClass().getResourceAsStream("/RailStations_latest.zip"));

        InputStream netexTimetableDataset = getClass().getResourceAsStream("/rb_flb-aggregated-netex.zip");

        GtfsExporter gtfsExport = new DefaultGtfsExporter("FLB", defaultStopAreaRepository);

        InputStream exportedGtfs = gtfsExport.convertNetexToGtfs(netexTimetableDataset);

        File gtfsFile = new File("export-gtfs.zip");
        java.nio.file.Files.copy(
                exportedGtfs,
                gtfsFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        checkAgency(gtfsFile);
        checkStop(gtfsFile);
        checkRoute(gtfsFile);
        checkTrip(gtfsFile);
        checkStopTime(gtfsFile);
        checkCalendarDate(gtfsFile);

        IOUtils.closeQuietly(exportedGtfs);
        Files.deleteIfExists(gtfsFile.toPath());
    }

    private void checkAgency(File gtfsFile) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "agency.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("agency_id"));
        Assertions.assertTrue(record.get("agency_id").startsWith("FLB:Authority"));
        Assertions.assertFalse(records.iterator().hasNext());
    }

    private void checkStop(File gtfsFile) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "stops.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("stop_id"));
        Assertions.assertTrue(record.get("stop_id").startsWith("NSR:Quay"));
    }

    private void checkRoute(File gtfsFile) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "routes.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("route_id"));
        Assertions.assertTrue(record.get("route_id").startsWith("FLB:Line"));
    }

    private void checkTrip(File gtfsFile) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "trips.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("route_id"));
        Assertions.assertTrue(record.get("route_id").startsWith("FLB:Line"));
    }

    private void checkCalendarDate(File gtfsFile) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "calendar_dates.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("service_id"));
        Assertions.assertTrue(record.get("service_id").startsWith("FLB:DayType:"));
    }

    private void checkStopTime(File gtfsFile) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "stop_times.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("trip_id"));
        Assertions.assertTrue(record.get("trip_id").startsWith("FLB:ServiceJourney"));
    }

    private Iterable<CSVRecord> getCsvRecords(File gtfsFile, String entryName) throws IOException {
        Assertions.assertTrue(ZipUtil.containsEntry(gtfsFile, entryName));

        byte[] zipEntry = ZipUtil.unpackEntry(gtfsFile, entryName, StandardCharsets.UTF_8);
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
        return csvFormat.parse(new InputStreamReader(new ByteArrayInputStream(zipEntry)));
    }
}
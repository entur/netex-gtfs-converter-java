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
    void testExportSimpleLine() throws IOException {
        testExport("FLB", "/rb_flb-aggregated-netex.zip", "/RailStations_latest.zip");
    }

    @Test
    void testExportDatedServiceJourney() throws IOException {
        testExport("VYG", "/rb_vyg-aggregated-netex.zip", "/RailStations_latest.zip");
    }

    @Test
    void testExportMultipleModePerLine() throws IOException {
        testExport("SJN", "/rb_sjn-aggregated-netex.zip", "/CurrentAndFuture_latest.zip");
    }

    @Test
    void testExportStops() throws IOException {
        DefaultStopAreaRepository defaultStopAreaRepository = new DefaultStopAreaRepository();
        defaultStopAreaRepository.loadStopAreas(getClass().getResourceAsStream("/RailStations_latest.zip"));
        GtfsExporter gtfsExport = new DefaultGtfsExporter(defaultStopAreaRepository);

        InputStream exportedGtfs = gtfsExport.convertStopsToGtfs();

        File gtfsFile = new File("export-gtfs.zip");
        java.nio.file.Files.copy(
                exportedGtfs,
                gtfsFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        checkStop(gtfsFile);
        IOUtils.closeQuietly(exportedGtfs);
        Files.deleteIfExists(gtfsFile.toPath());
    }

    void testExport(String codespace, String timetableDataset, String stopDataset) throws IOException {

        DefaultStopAreaRepository defaultStopAreaRepository = new DefaultStopAreaRepository();
        defaultStopAreaRepository.loadStopAreas(getClass().getResourceAsStream(stopDataset));

        InputStream netexTimetableDataset = getClass().getResourceAsStream(timetableDataset);

        GtfsExporter gtfsExport = new DefaultGtfsExporter(codespace, defaultStopAreaRepository);

        InputStream exportedGtfs = gtfsExport.convertTimetablesToGtfs(netexTimetableDataset);

        File gtfsFile = new File("export-gtfs.zip");
        java.nio.file.Files.copy(
                exportedGtfs,
                gtfsFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        checkAgency(gtfsFile, codespace);
        checkStop(gtfsFile);
        checkRoute(gtfsFile, codespace);
        checkTrip(gtfsFile, codespace);
        checkStopTime(gtfsFile, codespace);
        checkCalendarDate(gtfsFile, codespace);

        IOUtils.closeQuietly(exportedGtfs);
        Files.deleteIfExists(gtfsFile.toPath());
    }

    private void checkAgency(File gtfsFile, String codespace) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "agency.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("agency_id"));
        Assertions.assertTrue(record.get("agency_id").startsWith(codespace + ':' + "Authority"));
        Assertions.assertFalse(records.iterator().hasNext());
    }

    private void checkStop(File gtfsFile) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "stops.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("stop_id"));
        Assertions.assertTrue(record.get("stop_id").startsWith("NSR:Quay"));
    }

    private void checkRoute(File gtfsFile, String codespace) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "routes.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("route_id"));
        Assertions.assertTrue(record.get("route_id").startsWith(codespace + ':' + "Line"));
    }

    private void checkTrip(File gtfsFile, String codespace) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "trips.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("route_id"));
        Assertions.assertTrue(record.get("route_id").startsWith(codespace + ':' + "Line"));
    }

    private void checkCalendarDate(File gtfsFile, String codespace) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "calendar_dates.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        String serviceId = record.get("service_id");
        Assertions.assertNotNull(serviceId);
        Assertions.assertTrue(serviceId.startsWith(codespace + ':' + "DayType:") || serviceId.startsWith(codespace + ':' + "OperatingDay:"));
    }

    private void checkStopTime(File gtfsFile, String codespace) throws IOException {
        Iterable<CSVRecord> records = getCsvRecords(gtfsFile, "stop_times.txt");
        Assertions.assertTrue(records.iterator().hasNext());
        CSVRecord record = records.iterator().next();
        Assertions.assertNotNull(record.get("trip_id"));
        Assertions.assertTrue(record.get("trip_id").startsWith(codespace + ':' + "ServiceJourney"));
    }

    private Iterable<CSVRecord> getCsvRecords(File gtfsFile, String entryName) throws IOException {
        Assertions.assertTrue(ZipUtil.containsEntry(gtfsFile, entryName));

        byte[] zipEntry = ZipUtil.unpackEntry(gtfsFile, entryName, StandardCharsets.UTF_8);
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
        return csvFormat.parse(new InputStreamReader(new ByteArrayInputStream(zipEntry)));
    }
}

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

import org.entur.netex.gtfs.export.converters.StopsToGtfsConverter;
import org.entur.netex.gtfs.export.converters.TimetablesToGtfsConverter;
import org.entur.netex.gtfs.export.loader.DefaultNetexDatasetLoader;
import org.entur.netex.gtfs.export.producer.*;
import org.entur.netex.gtfs.export.repository.DefaultGtfsRepository;
import org.entur.netex.gtfs.export.repository.DefaultNetexDatasetRepository;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.stop.StopAreaRepository;
import org.entur.netex.gtfs.export.stop.StopAreaRepositoryFactory;
import org.onebusaway.gtfs.model.FeedInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class DefaultGtfsExporter implements GtfsExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGtfsExporter.class);
    private final StopAreaRepositoryFactory stopAreaRepositoryFactory;
    private final GtfsDatasetRepository gtfsDatasetRepository;
    private final FeedInfoProducer feedInfoProducer;

    public DefaultGtfsExporter(StopAreaRepositoryFactory stopAreaRepositoryFactory,
                               FeedInfoProducer feedInfoProducer) {
        this.stopAreaRepositoryFactory = stopAreaRepositoryFactory;
        this.feedInfoProducer = feedInfoProducer;
        this.gtfsDatasetRepository = new DefaultGtfsRepository();
    }

    @Override
    public InputStream convertTimetablesToGtfs(String codespace,
                                               InputStream netexTimetableDataset,
                                               boolean generateStaySeatedTransfer) {
        if (codespace == null) {
            throw new IllegalStateException("Missing required codespace for timetable data export");
        }

        StopAreaRepository stopAreaRepository = stopAreaRepositoryFactory.getStopAreaRepository();
        NetexDatasetRepository netexDatasetRepository = new DefaultNetexDatasetRepository();

        loadNetexTimetableDatasetToRepository(netexTimetableDataset, netexDatasetRepository);

        DefaultGtfsServiceRepository gtfsServiceRepository = new DefaultGtfsServiceRepository(
                codespace,
                netexDatasetRepository);

        TimetablesToGtfsConverter timetablesToGtfsConverter = new TimetablesToGtfsConverter(
                netexDatasetRepository,
                gtfsDatasetRepository,
                stopAreaRepository,
                gtfsServiceRepository,
                generateStaySeatedTransfer
        );
        timetablesToGtfsConverter.convert();
        addFeedInfo();

        return gtfsDatasetRepository.writeGtfs();
    }

    protected void loadNetexTimetableDatasetToRepository(InputStream netexTimetableDataset,
                                                         NetexDatasetRepository netexDatasetRepository) {
        LOGGER.info("Importing NeTEx Timetable dataset");
        DefaultNetexDatasetLoader netexDatasetLoader = new DefaultNetexDatasetLoader();
        netexDatasetLoader.load(netexTimetableDataset, netexDatasetRepository);
        LOGGER.info("Imported NeTEx Timetable dataset");
    }

    @Override
    public InputStream convertStopsToGtfs() {
        StopAreaRepository stopAreaRepository = stopAreaRepositoryFactory.getStopAreaRepository();
        StopsToGtfsConverter stopsToGtfsConverter = new StopsToGtfsConverter(stopAreaRepository, gtfsDatasetRepository);
        stopsToGtfsConverter.convert();
        addFeedInfo();
        return gtfsDatasetRepository.writeGtfs();
    }

    protected void addFeedInfo() {
        if (feedInfoProducer != null) {
            FeedInfo feedInfo = feedInfoProducer.produceFeedInfo();
            if (feedInfo != null) {
                gtfsDatasetRepository.saveEntity(feedInfo);
            }
        }
    }

    @Override
    public GtfsDatasetRepository getGtfsDatasetRepository() {
        return gtfsDatasetRepository;
    }
}

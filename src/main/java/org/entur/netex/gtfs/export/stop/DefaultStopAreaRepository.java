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

package org.entur.netex.gtfs.export.stop;

import org.entur.netex.gtfs.export.exception.QuayNotFoundException;
import org.entur.netex.gtfs.export.exception.StopPlaceNotFoundException;
import org.entur.netex.gtfs.export.loader.DefaultNetexDatasetLoader;
import org.entur.netex.gtfs.export.loader.NetexDatasetLoader;
import org.entur.netex.gtfs.export.repository.DefaultNetexDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A Stop area repository that loads data from a NeTEx dataset archive.
 */
public class DefaultStopAreaRepository implements StopAreaRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStopAreaRepository.class);

    private Map<String, StopPlace> stopPlaceByQuayId;
    private Map<String, Quay> quayById;

    private final NetexDatasetLoader netexDatasetLoader;


    public DefaultStopAreaRepository() {
        this.netexDatasetLoader = new DefaultNetexDatasetLoader();
    }

    public void loadStopAreas(InputStream stopDataset) {

        LOGGER.info("Importing NeTEx Stop dataset");
        NetexDatasetRepository netexStopRepository = new DefaultNetexDatasetRepository();
        netexDatasetLoader.load(stopDataset, netexStopRepository);
        NetexEntitiesIndex netexStopEntitiesIndex = netexStopRepository.getIndex();

        stopPlaceByQuayId = netexStopEntitiesIndex.getStopPlaceIdByQuayIdIndex()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> netexStopEntitiesIndex.getStopPlaceIndex().getLatestVersion(entry.getValue())));

        quayById = netexStopEntitiesIndex.getQuayIndex().getAllVersions()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> netexStopEntitiesIndex.getQuayIndex().getLatestVersion(entry.getKey())));

        LOGGER.info("Imported NeTEx Stop dataset");

    }

    @Override
    public StopPlace getStopPlaceByQuayId(String quayId) {
        StopPlace stopPlace = stopPlaceByQuayId.get(quayId);
        if (stopPlace == null) {
            throw new StopPlaceNotFoundException("Could not find StopPlace for quay id " + quayId);
        }
        return stopPlace;
    }

    @Override
    public Quay getQuayById(String quayId) {
        Quay quay = quayById.get(quayId);
        if (quay == null) {
            throw new QuayNotFoundException("Could not find Quay for id " + quayId);
        }
        return quay;
    }
}

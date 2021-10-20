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

package org.entur.netex.gtfs.export.loader;

import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;

import java.io.InputStream;

/**
 * Load a NeTEx dataset into memory.
 */
public interface NetexDatasetLoader {

    /**
     * Load a NeTEX dataset archive into an in-memory repository
     * @param timetableDataset a ZIP file containing the NeTEx dataset
     * @param netexDatasetRepository an in-memory repository containing the NeTEx entities.
     */
    void load(InputStream timetableDataset, NetexDatasetRepository netexDatasetRepository);
}

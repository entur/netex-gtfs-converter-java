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

import java.io.InputStream;

/**
 * Factory for creating stop area repositories.
 */
public interface StopAreaRepositoryFactory {

    /**
     * Return an initialized instance of a stop area repository.
     * Multiple calls to the method may return different repositories if the underlying implementation allows for refreshing the dataset at runtime.
     *
     * @return an initialized instance of a stop area repository.
     */
    StopAreaRepository getStopAreaRepository();

    /**
     * Refresh the cached stop area.
     *
     * @param stopDataset an input stream on a NeTEX dataset archive.
     */
    void refreshStopAreaRepository(InputStream stopDataset);
}

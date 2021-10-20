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

import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.onebusaway.gtfs.model.Stop;

/**
 * Utility for GTFS Stop.
 */
public final class StopUtil {

    private StopUtil() {
    }

    /**
     * Return the GTFS stop corresponding to a given Scheduled Stop Point.
     * @param scheduledStopPointId the Scheduled Stop Point.
     * @param netexDatasetRepository the NeTEx dataset repository.
     * @param gtfsDatasetRepository the GTFS dataset repository.
     * @return the GTFS stop corresponding to the Scheduled Stop Point.
     */
    public static Stop getGtfsStopFromScheduledStopPointId(String scheduledStopPointId, NetexDatasetRepository netexDatasetRepository, GtfsDatasetRepository gtfsDatasetRepository) {
        String quayId = netexDatasetRepository.getQuayIdByScheduledStopPointId(scheduledStopPointId);
        return gtfsDatasetRepository.getStopById(quayId);
    }
}

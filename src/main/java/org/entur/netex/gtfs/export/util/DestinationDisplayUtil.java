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

import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for Destination Displays.
 */
public final class DestinationDisplayUtil {

    private DestinationDisplayUtil() {
    }

    /**
     * Return the destination display on the first stop in the journey pattern.
     * @param journeyPattern the journey pattern.
     * @param netexDatasetRepository the repository of NeTEx data.
     * @return the destination display on the first stop in the journey pattern.
     */
    public static DestinationDisplay getInitialDestinationDisplay(JourneyPattern journeyPattern, NetexDatasetRepository netexDatasetRepository) {
        StopPointInJourneyPattern firstStopPointInJourneyPattern = (StopPointInJourneyPattern) journeyPattern.getPointsInSequence()
                .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern()
                .get(0);
        return netexDatasetRepository.getDestinationDisplayById(firstStopPointInJourneyPattern.getDestinationDisplayRef().getRef());
    }

    /**
     * Compute a front text (GTFS head sign) from a destination display that may contain a list of vias (intermediate head sign)
     *
     * @param destinationDisplay     the NeTEx destination display
     * @param netexDatasetRepository the netex dataset repository
     * @return a front text that concatenates the destination display and its optional vias.
     */
    public static String getFrontTextWithComputedVias(DestinationDisplay destinationDisplay, NetexDatasetRepository netexDatasetRepository) {

        if (destinationDisplay == null) {
            return null;
        }

        List<DestinationDisplay> vias;
        if (destinationDisplay.getVias() != null) {
            vias = destinationDisplay.getVias()
                    .getVia()
                    .stream()
                    .map(via -> via.getDestinationDisplayRef().getRef())
                    .map(netexDatasetRepository::getDestinationDisplayById)
                    .collect(Collectors.toList());
        } else {
            vias = Collections.emptyList();
        }
        String frontText = destinationDisplay.getFrontText().getValue();
        if (!vias.isEmpty() && frontText != null) {
            StringBuilder b = new StringBuilder();
            b.append(frontText);
            b.append(" via ");
            List<String> viaFrontTexts = new ArrayList<>();
            for (DestinationDisplay via : vias) {
                if (via.getFrontText() != null) {
                    viaFrontTexts.add(via.getFrontText().getValue());
                }
            }
            b.append(String.join("/", viaFrontTexts));
            return b.toString();
        } else {
            return frontText;
        }
    }
}

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

import java.util.Objects;
import java.util.stream.Collectors;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DestinationDisplay_VersionStructure;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

/**
 * Utility class for Destination Displays.
 */
public final class DestinationDisplayUtil {

  private DestinationDisplayUtil() {}

  /**
   * Return the destination display on the first stop in the journey pattern.
   *
   * @param journeyPattern         the journey pattern.
   * @param netexDatasetRepository the repository of NeTEx data.
   * @return the destination display on the first stop in the journey pattern.
   */
  public static DestinationDisplay getInitialDestinationDisplay(
    JourneyPattern journeyPattern,
    NetexDatasetRepository netexDatasetRepository
  ) {
    StopPointInJourneyPattern firstStopPointInJourneyPattern =
      (StopPointInJourneyPattern) journeyPattern
        .getPointsInSequence()
        .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern()
        .get(0);
    return netexDatasetRepository.getDestinationDisplayById(
      firstStopPointInJourneyPattern.getDestinationDisplayRef().getRef()
    );
  }

  /**
   * Build the GTFS head sign from a destination display that may contain a list of vias (intermediate head signs).
   * If the destination display front text is A and it refers to 2 Vias with front texts B and C, then the head sign is "A via B/C".
   *
   * @param destinationDisplay     the NeTEx destination display
   * @param netexDatasetRepository the netex dataset repository
   * @return a head sign that concatenates the destination display front text and its optional vias.
   */
  public static String getHeadSignFromDestinationDisplay(
    DestinationDisplay destinationDisplay,
    NetexDatasetRepository netexDatasetRepository
  ) {
    if (destinationDisplay == null) {
      return null;
    }
    String frontText = destinationDisplay.getFrontText().getValue();
    String via = "";
    if (destinationDisplay.getVias() != null) {
      via =
        destinationDisplay
          .getVias()
          .getVia()
          .stream()
          .map(netexVia -> netexVia.getDestinationDisplayRef().getRef())
          .map(netexDatasetRepository::getDestinationDisplayById)
          .map(DestinationDisplay_VersionStructure::getFrontText)
          .filter(Objects::nonNull)
          .map(MultilingualString::getValue)
          .collect(Collectors.joining("/"));
    }

    if (!via.isEmpty()) {
      frontText = frontText + " via " + via;
    }
    return frontText;
  }
}

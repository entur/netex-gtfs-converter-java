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

package org.entur.netex.gtfs.export.model;

import java.util.List;
import java.util.Map;
import org.entur.netex.gtfs.export.exception.GtfsExportException;
import org.onebusaway.gtfs.model.ShapePoint;

/**
 * A GTFS shape made of a list of shape points.
 *
 */
public class GtfsShape {

  private final String id;
  private final List<ShapePoint> shapePoints;
  private final Map<Integer, Double> distanceTravelledByStopOrder;

  public GtfsShape(
    String id,
    List<ShapePoint> shapePoints,
    Map<Integer, Double> distanceTravelledByStopOrder
  ) {
    this.id = id;
    this.shapePoints = shapePoints;
    this.distanceTravelledByStopOrder = distanceTravelledByStopOrder;
  }

  public String getId() {
    return id;
  }

  public List<ShapePoint> getShapePoints() {
    return shapePoints;
  }

  /**
   * Return the distance travelled on the shape from the start to the stop with the given order.
   * The distance travelled to the first stop in the JourneyPattern is 0 meters.
   * The lookup is keyed by the NeTEx {@code order} value of the StopPointInJourneyPattern, which is
   * not required to start at 1 or to be gap-free.
   * @param order the NeTEx order of the stop in the JourneyPattern.
   * @return the distance travelled on the shape from the start to this stop, in meters.
   */
  public double getDistanceTravelledToStop(int order) {
    Double distance = distanceTravelledByStopOrder.get(order);
    if (distance == null) {
      throw new GtfsExportException(
        "No travelled distance for stop order " + order + " in shape " + id
      );
    }
    return distance;
  }
}

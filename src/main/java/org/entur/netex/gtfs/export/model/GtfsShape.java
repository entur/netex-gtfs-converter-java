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

import org.onebusaway.gtfs.model.ShapePoint;

import java.util.List;

/**
 * A GTFS shape made of a list of shape points.
 *
 */
public class GtfsShape {

    private final String id;
    private final List<ShapePoint> shapePoints;
    private final List<Double> travelledDistanceToStop;


    public GtfsShape(String id, List<ShapePoint> shapePoints, List<Double> travelledDistanceToStop) {
        this.id = id;
        this.shapePoints = shapePoints;
        this.travelledDistanceToStop = travelledDistanceToStop;
    }

    public String getId() {
        return id;
    }

    public List<ShapePoint> getShapePoints() {
        return shapePoints;
    }


    /**
     * Return the distance travelled on the shape from the start to the stop number i.
     * The distance travelled to stop number 1 is 0 meters.
     * @param i sequence number of the stop in the JourneyPattern, starting at 1.
     * @return the distance travelled on the shape from the start to the stop number i, in meters.
     */
    public double getDistanceTravelledToStop(int i) {
        return travelledDistanceToStop.get(i-1);
        }
}

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

import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.DirectPositionType;
import net.opengis.gml._3.LineStringType;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFilter;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for geometric conversions.
 */
public final class GeometryUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryUtil.class);

    /**
     * SRID for WGS84 as string and code.
     */
    private static final String DEFAULT_SRID_NAME = "WGS84";
    private static final String DEFAULT_SRID_AS_STRING = "4326";
    private static final int DEFAULT_SRID_AS_INT = 4326;

    /**
     * Earth radius on the equator for the WGS84 system, in meters.
     */
    private static final int EQUATORIAL_RADIUS = 6378137;

    /**
     * A geometry factory based on the WGS84 system.
     */
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), DEFAULT_SRID_AS_INT);

    private GeometryUtil() {
    }

    /**
     * Calculate the distance between 2 coordinates, in meters.
     *
     * @param from from coordinate
     * @param to   to coordinate
     * @return the distance between the 2 coordinates, in meters.
     */
    public static double distance(Coordinate from, Coordinate to) {
        if (from == null) {
            return 0;
        }
        LineString lineString = GEOMETRY_FACTORY.createLineString(new Coordinate[]{from, to});
        return lineString.getLength() * (Math.PI / 180) * EQUATORIAL_RADIUS;
    }

    /**
     * Convert a GML LIneString object into a JTS LineString.
     *
     * @param gml the GML LineString.
     * @return the JTS LineString.
     */
    public static LineString convertLineStringFromGmlToJts(LineStringType gml) {
        List<Double> coordinateList;
        DirectPositionListType posList = gml.getPosList();
        if (posList != null && !posList.getValue().isEmpty()) {
            coordinateList = posList.getValue();
        } else {
            if (gml.getPosOrPointProperty() != null && !gml.getPosOrPointProperty().isEmpty()) {
                coordinateList = new ArrayList<>();
                for (Object o : gml.getPosOrPointProperty()) {
                    if (o instanceof DirectPositionType) {
                        DirectPositionType directPositionType = (DirectPositionType) o;
                        coordinateList.addAll(directPositionType.getValue());
                    } else {
                        LOGGER.warn("Unknown class ({}) for PosOrPointProperty for gmlString {}", o.getClass(), gml.getId());
                    }
                }
                if (coordinateList.isEmpty()) {
                    LOGGER.warn("Unknown class in PosOrPointProperty for gmlString {}", gml.getId());
                    return null;
                }

            } else {
                LOGGER.warn("LineStringType without posList or PosOrPointProperty for gmlString {}", gml.getId());
                return null;
            }
        }

        CoordinateSequence coordinateSequence = new PackedCoordinateSequenceFactory().create(coordinateList.stream().mapToDouble(Double::doubleValue).toArray(), 2);
        LineString jts = new LineString(coordinateSequence, GEOMETRY_FACTORY);
        jts.apply(new SwapPackedCoordinateSequenceFilter());
        assignSRID(gml, jts);

        return jts;
    }

    /**
     * Assign an SRID to the LineString based on the provided Spatial Reference System name.
     * The LineString is expected to be based on the WGS84 spatial reference system (SRID=4326).
     * If srsName is not set, the SRID defaults to 4326 (default value set by the {@link GeometryFactory}).
     * If srsName is set to either "4326" or "WGS84", the SRID defaults to 4326.
     * If srsName is set to another value, an attempt is made to parse it as a SRID.
     * If srsName is not parseable as a SRID, then the SRID defaults to 4326.
     **/
    private static void assignSRID(LineStringType gml, LineString jts) {
        String srsName = gml.getSrsName();
        if (!StringUtils.isEmpty(srsName) && !DEFAULT_SRID_NAME.equals(srsName) && !DEFAULT_SRID_AS_STRING.equals(srsName)) {
            LOGGER.warn("The LineString {} is not based on the WGS84 Spatial Reference System. SRID in use: {}", gml.getId(), srsName);
            try {
                jts.setSRID(Integer.parseInt(srsName));
            } catch (NumberFormatException nfe) {
                LOGGER.warn("Ignoring SRID on linestring {} for illegal value: {}", gml.getId(), srsName);
            }
        }
    }


    /**
     * Swap latitude and longitude since GML and JTS have reversed convention.
     */
    private static final class SwapPackedCoordinateSequenceFilter implements CoordinateSequenceFilter {

        private SwapPackedCoordinateSequenceFilter() {
        }

        @Override
        public void filter(CoordinateSequence coordinateSequence, int i) {
            PackedCoordinateSequence packedCoordinateSequence = (PackedCoordinateSequence) coordinateSequence;
            double originalCoordinateX = packedCoordinateSequence.getX(i);
            packedCoordinateSequence.setX(i, coordinateSequence.getY(i));
            packedCoordinateSequence.setY(i, originalCoordinateX);
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean isGeometryChanged() {
            return true;
        }
    }
}

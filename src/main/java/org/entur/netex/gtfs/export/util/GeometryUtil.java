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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.DirectPositionType;
import net.opengis.gml._3.LineStringType;
import net.opengis.gml._3.PointPropertyType;
import org.apache.commons.lang3.StringUtils;
import org.entur.netex.gtfs.export.exception.GtfsExportException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
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

/**
 * Utility class for geometric conversions.
 */
public final class GeometryUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    GeometryUtil.class
  );

  /**
   * SRID for WGS84 as string and code.
   */
  private static final String DEFAULT_SRID_NAME = "WGS84";
  private static final String DEFAULT_SRID_AS_STRING = "4326";
  private static final int DEFAULT_SRID_AS_INT = 4326;

  /**
   * A geometry factory based on the WGS84 system.
   */
  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(
    new PrecisionModel(),
    DEFAULT_SRID_AS_INT
  );

  private GeometryUtil() {}

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

    try {
      return JTS.orthodromicDistance(from, to, DefaultGeographicCRS.WGS84);
    } catch (TransformException e) {
      throw new GtfsExportException(e);
    }
  }

  /**
   * Return a JTS LineString corresponding to the GML LineString, or null if the GML LineString is invalid
   *
   * @param gmlLineString the GML LineString.
   * @return the JTS LineString or null if the GML LineString is invalid.
   */
  public static LineString convertLineStringFromGmlToJts(
    LineStringType gmlLineString
  ) {
    List<Double> coordinates = extractCoordinates(gmlLineString);
    if (coordinates.isEmpty()) {
      return null;
    }
    CoordinateSequence coordinateSequence =
      new PackedCoordinateSequenceFactory()
        .create(
          coordinates.stream().mapToDouble(Double::doubleValue).toArray(),
          2
        );
    LineString jts = new LineString(coordinateSequence, GEOMETRY_FACTORY);
    jts.apply(new SwapPackedCoordinateSequenceFilter());
    assignSRID(gmlLineString, jts);

    return jts;
  }

  /**
   * Extract the GML coordinates as a list of double values.
   *
   * @param gmlLineString a GML LineString
   * @return a list of coordinates. The list is empty if the LineString is not valid.
   */
  private static List<Double> extractCoordinates(LineStringType gmlLineString) {
    List<Double> coordinates;
    DirectPositionListType posList = gmlLineString.getPosList();
    if (posList != null && !posList.getValue().isEmpty()) {
      coordinates = posList.getValue();
    } else {
      if (
        gmlLineString.getPosOrPointProperty() != null &&
        !gmlLineString.getPosOrPointProperty().isEmpty()
      ) {
        coordinates = new ArrayList<>();
        for (Object o : gmlLineString.getPosOrPointProperty()) {
          if (o instanceof DirectPositionType directPositionType) {
            coordinates.addAll(directPositionType.getValue());
          } else if (o instanceof PointPropertyType) {
            LOGGER.warn(
              "Unsupported PointPropertyType for gmlString {}",
              gmlLineString.getId()
            );
            return Collections.emptyList();
          } else {
            LOGGER.warn(
              "Unknown class ({}) for PosOrPointProperty for gmlString {}",
              o.getClass(),
              gmlLineString.getId()
            );
            return Collections.emptyList();
          }
        }
        if (coordinates.isEmpty()) {
          LOGGER.warn(
            "LineStringType without coordinates for gmlString {}",
            gmlLineString.getId()
          );
        }
      } else {
        LOGGER.warn(
          "LineStringType without posList or PosOrPointProperty for gmlString {}",
          gmlLineString.getId()
        );
        return Collections.emptyList();
      }
    }
    return coordinates;
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
    if (
      !StringUtils.isEmpty(srsName) &&
      !DEFAULT_SRID_NAME.equals(srsName) &&
      !DEFAULT_SRID_AS_STRING.equals(srsName)
    ) {
      LOGGER.warn(
        "The LineString {} is not based on the WGS84 Spatial Reference System. SRID in use: {}",
        gml.getId(),
        srsName
      );
      try {
        jts.setSRID(Integer.parseInt(srsName));
      } catch (NumberFormatException nfe) {
        LOGGER.warn(
          "Ignoring SRID on linestring {} for illegal value: {}",
          gml.getId(),
          srsName
        );
      }
    }
  }

  /**
   * Swap latitude and longitude since GML and JTS have reversed convention.
   */
  private static final class SwapPackedCoordinateSequenceFilter
    implements CoordinateSequenceFilter {

    private SwapPackedCoordinateSequenceFilter() {}

    @Override
    public void filter(CoordinateSequence coordinateSequence, int i) {
      PackedCoordinateSequence packedCoordinateSequence =
        (PackedCoordinateSequence) coordinateSequence;
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

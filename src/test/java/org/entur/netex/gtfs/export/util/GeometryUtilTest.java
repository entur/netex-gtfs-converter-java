package org.entur.netex.gtfs.export.util;

import java.math.BigInteger;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.DirectPositionType;
import net.opengis.gml._3.LineStringType;
import net.opengis.gml._3.PointPropertyType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

class GeometryUtilTest {

  @Test
  void testGmlToJtsWithPosListLineString() {
    LineStringType gmlLineString = new LineStringType()
      .withId("gmlLineString")
      .withPosList(
        new DirectPositionListType()
          .withSrsDimension(BigInteger.valueOf(2L))
          .withValue(10.0, 120.5, 11.0, 121.5)
      );
    LineString jtsLineString = GeometryUtil.convertLineStringFromGmlToJts(
      gmlLineString
    );
    Assertions.assertNotNull(jtsLineString);
    Assertions.assertEquals(2, jtsLineString.getCoordinates().length);
    Assertions.assertEquals(
      new Coordinate(120.5, 10),
      jtsLineString.getCoordinates()[0]
    );
    Assertions.assertEquals(
      new Coordinate(121.5, 11.0),
      jtsLineString.getCoordinates()[1]
    );
  }

  @Test
  void testGmlToJtsWitPosOrPointLineString() {
    LineStringType gmlLineString = new LineStringType()
      .withId("gmlLineString")
      .withPosOrPointProperty(
        new DirectPositionType()
          .withSrsDimension(BigInteger.valueOf(2L))
          .withValue(10.0, 120.5, 11.0, 121.5)
      );
    LineString jtsLineString = GeometryUtil.convertLineStringFromGmlToJts(
      gmlLineString
    );
    Assertions.assertNotNull(jtsLineString);
    Assertions.assertEquals(2, jtsLineString.getCoordinates().length);
    Assertions.assertEquals(
      new Coordinate(120.5, 10),
      jtsLineString.getCoordinates()[0]
    );
    Assertions.assertEquals(
      new Coordinate(121.5, 11.0),
      jtsLineString.getCoordinates()[1]
    );
  }

  @Test
  void testGmlToJtsWithUnsupportedLineString() {
    LineStringType gmlLineString = new LineStringType()
      .withId("gmlLineString")
      .withPosOrPointProperty(new PointPropertyType());
    LineString jtsLineString = GeometryUtil.convertLineStringFromGmlToJts(
      gmlLineString
    );
    Assertions.assertNull(jtsLineString);
  }

  @Test
  void testGmlToJtsWithEmptyLineString() {
    LineStringType gmlLineString = new LineStringType().withId("gmlLineString");
    LineString jtsLineString = GeometryUtil.convertLineStringFromGmlToJts(
      gmlLineString
    );
    Assertions.assertNull(jtsLineString);
  }

  @Test
  void testDistance() {
    Coordinate from = new Coordinate(10.784823, 59.963926);
    Coordinate to = new Coordinate(10.784564, 59.963652);

    Assertions.assertEquals(34, Math.round(GeometryUtil.distance(from, to)));
  }
}

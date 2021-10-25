package org.entur.netex.gtfs.export.util;

import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

import java.math.BigInteger;

class GeometryUtilTest {

    @Test
    void testGmlToJts() {
        LineStringType gmlLineString = new LineStringType()
                .withId("gmlLineString")
                .withPosList(new DirectPositionListType()
                        .withSrsDimension(BigInteger.valueOf(2L))
                        .withValue(10.0, 120.5, 11.0, 121.5));
        LineString jtsLineString = GeometryUtil.convertLineStringFromGmlToJts(gmlLineString);
        Assertions.assertNotNull(jtsLineString);
        Assertions.assertEquals(2, jtsLineString.getCoordinates().length);
        Assertions.assertEquals(new Coordinate(120.5, 10), jtsLineString.getCoordinates()[0]);
        Assertions.assertEquals(new Coordinate(121.5, 11.0), jtsLineString.getCoordinates()[1]);
    }


}

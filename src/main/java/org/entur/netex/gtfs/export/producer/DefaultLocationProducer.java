/*
 *
 *  *
 *  *  * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 *  *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  *  * You may not use this work except in compliance with the Licence.
 *  *  * You may obtain a copy of the Licence at:
 *  *  *
 *  *  *   https://joinup.ec.europa.eu/software/page/eupl
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the Licence for the specific language governing permissions and
 *  *  * limitations under the Licence.
 *  *  *
 *  *
 *
 */

package org.entur.netex.gtfs.export.producer;

import org.entur.netex.gtfs.export.exception.GtfsExportException;
import org.entur.netex.gtfs.export.util.GeometryUtil;
import org.geojson.LngLatAlt;
import org.locationtech.jts.geom.Polygon;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Location;
import org.rutebanken.netex.model.FlexibleArea;
import org.rutebanken.netex.model.FlexibleStopPlace;
import org.rutebanken.netex.model.HailAndRideArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultLocationProducer implements LocationProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLocationProducer.class);

    public Location produceLocation(FlexibleStopPlace flexibleStopPlace) {

        List<Object> flexibleAndHailAndRideAreas = flexibleStopPlace.getAreas().getFlexibleAreaOrFlexibleAreaRefOrHailAndRideArea();
        if (flexibleAndHailAndRideAreas.size() == 0) {
            throw new GtfsExportException("Missing stop area in FlexibleStopPlace " + flexibleStopPlace.getId());
        }
        if (flexibleAndHailAndRideAreas.size() > 1) {
            throw new GtfsExportException("More than one stop area in FlexibleStopPlace " + flexibleStopPlace.getId());
        }
        Object o = flexibleAndHailAndRideAreas.get(0);
        if (o instanceof FlexibleArea) {
            FlexibleArea flexibleArea = (FlexibleArea) o;
            return createLocationFromFlexibleArea(flexibleArea, flexibleStopPlace.getId());
        } else if (o instanceof HailAndRideArea) {
            LOGGER.warn("Ignoring unsupported HailAndRideArea for flexibleStopPlace {}", flexibleStopPlace.getId());
            return null;
        } else {
            LOGGER.warn("Ignoring unsupported FlexibleAreaType {} for FlexibleStopPlace {}", o.getClass(), flexibleStopPlace.getId());
            return null;
        }
    }

    private Location createLocationFromFlexibleArea(FlexibleArea flexibleArea, String id) {
        Polygon jtsPolygon = GeometryUtil.convertPolygonFromGmlToJts(flexibleArea.getPolygon());
        Location location = new Location();
        location.setId(new AgencyAndId("ENT",id));
        org.geojson.Polygon aPolygon = new org.geojson.Polygon();
        List<LngLatAlt> exteriorRingCoordinates = Arrays.stream(jtsPolygon.getExteriorRing().getCoordinates())
                .map(coordinate -> new LngLatAlt(coordinate.getX(), coordinate.getY()))
                .collect(Collectors.toList());
        aPolygon.setExteriorRing(exteriorRingCoordinates);
        location.setGeometry(aPolygon);
        return location;
    }
}

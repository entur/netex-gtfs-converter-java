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

package org.entur.netex.gtfs.export.producer;

import org.entur.netex.gtfs.export.model.GtfsShape;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.util.GeometryUtil;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ShapePoint;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.LinkInLinkSequence_VersionedChildStructure;
import org.rutebanken.netex.model.LinkSequenceProjection;
import org.rutebanken.netex.model.Projections_RelStructure;
import org.rutebanken.netex.model.ServiceLink;
import org.rutebanken.netex.model.ServiceLinkInJourneyPattern_VersionedChildStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class DefaultShapeProducer implements ShapeProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultShapeProducer.class);

    private final Agency agency;
    private final NetexDatasetRepository netexDatasetRepository;

    public DefaultShapeProducer(NetexDatasetRepository netexDatasetRepository, GtfsDatasetRepository gtfsDatasetRepository) {
        this.agency = gtfsDatasetRepository.getDefaultAgency();
        this.netexDatasetRepository = netexDatasetRepository;
    }

    @Override
    public GtfsShape produce(JourneyPattern journeyPattern) {
        int nbStopPoints = journeyPattern.getPointsInSequence().getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern().size();
        if (journeyPattern.getLinksInSequence() == null
                || journeyPattern.getLinksInSequence().getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern() == null
                || journeyPattern.getLinksInSequence().getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern().size() != (nbStopPoints - 1)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipping GTFS shape org.entur.netex.gtfs.org.entur.netex.gtfs.export for JourneyPattern {} with incomplete list of service links", journeyPattern.getId());
            }
            return null;
        }
        List<ShapePoint> shapePoints = new ArrayList<>();
        List<Double> travelledDistanceToStop = new ArrayList<>(nbStopPoints);
        // distance travelled to first stop is 0.
        travelledDistanceToStop.add(0.0);
        String shapeId = journeyPattern.getId();
        int sequence = 0;
        double distanceFromStart = 0;
        Coordinate previousPoint = null;
        for (LinkInLinkSequence_VersionedChildStructure link : journeyPattern.getLinksInSequence().getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern()) {
            ServiceLinkInJourneyPattern_VersionedChildStructure serviceLinkInJourneyPattern = (ServiceLinkInJourneyPattern_VersionedChildStructure) link;
            ServiceLink serviceLink = netexDatasetRepository.getServiceLinkById(serviceLinkInJourneyPattern.getServiceLinkRef().getRef());
            Projections_RelStructure projections = serviceLink.getProjections();
            if (projections == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Skipping GTFS shape org.entur.netex.gtfs.org.entur.netex.gtfs.export for JourneyPattern {} with service link {} without LineString", journeyPattern.getId(), serviceLink.getId());
                }
                return null;
            }
            for (JAXBElement<?> jaxbElement : projections.getProjectionRefOrProjection()) {
                LinkSequenceProjection linkSequenceProjection = (LinkSequenceProjection) jaxbElement.getValue();
                LineString lineString = GeometryUtil.convertLineStringFromGmlToJts(linkSequenceProjection.getLineString());
                if(lineString == null) {
                    return null;
                }
                for (Coordinate currentPoint : lineString.getCoordinates()) {
                    // the first point of the current link is the last point of the previous link, it can be skipped.
                    // as a side effect, duplicate points that follow one another are also filtered out
                    if (currentPoint.equals(previousPoint)) {
                        continue;
                    }
                    distanceFromStart += GeometryUtil.distance(previousPoint, currentPoint);
                    ShapePoint shapePoint = new ShapePoint();
                    AgencyAndId agencyAndId = new AgencyAndId();
                    agencyAndId.setId(shapeId);
                    agencyAndId.setAgencyId(agency.getId());
                    shapePoint.setShapeId(agencyAndId);
                    shapePoint.setSequence(sequence);
                    shapePoint.setLon(currentPoint.getX());
                    shapePoint.setLat(currentPoint.getY());
                    shapePoint.setDistTraveled(Math.round(distanceFromStart));
                    shapePoints.add(shapePoint);
                    sequence++;
                    previousPoint = currentPoint;
                }
            }
            travelledDistanceToStop.add((double) Math.round(distanceFromStart));
        }
        return new GtfsShape(shapeId, shapePoints, travelledDistanceToStop);
    }

}

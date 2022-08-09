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

import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
import org.apache.commons.lang3.ArrayUtils;
import org.entur.netex.gtfs.export.model.GtfsShape;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.util.GeometryUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.onebusaway.gtfs.model.Agency;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.LinkSequenceProjection;
import org.rutebanken.netex.model.LinksInJourneyPattern_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PointInLinkSequence_VersionedChildStructure;
import org.rutebanken.netex.model.PointsInJourneyPattern_RelStructure;
import org.rutebanken.netex.model.Projections_RelStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ServiceLink;
import org.rutebanken.netex.model.ServiceLinkInJourneyPattern_VersionedChildStructure;
import org.rutebanken.netex.model.ServiceLinkRefStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShapeProducerTest {

    private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();
    public static final String TEST_SERVICE_LINK_1 = "SERVICE_LINK_1";
    public static final String TEST_SERVICE_LINK_2 = "SERVICE_LINK_2";

    private static final Coordinate A1 = new Coordinate(59.72215, 10.512689);
    private static final Coordinate A2 = new Coordinate(59.722111, 10.512651);
    private static final Coordinate A3 = new Coordinate(59.721984, 10.512528);
    private static final Coordinate A4 = new Coordinate(59.721627, 10.512238);


    /**
     * Create a JourneyPattern with 3 stops linked by 2 service links.
     * The first service link contains 3 points, the second one contains 2 points.
     */
    @Test
    void testShapeProducer() {

        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);

        when(netexDatasetRepository.getServiceLinkById(TEST_SERVICE_LINK_1)).thenReturn(createServiceLink(A1.x, A1.y, A2.x, A2.y, A3.x, A3.y));
        when(netexDatasetRepository.getServiceLinkById(TEST_SERVICE_LINK_2)).thenReturn(createServiceLink(A3.x, A3.y, A4.x, A4.y));

        double serviceLinkLength1 = GeometryUtil.distance(A1, A2) + GeometryUtil.distance(A2, A3);
        double serviceLinkLength2 = GeometryUtil.distance(A3, A4);

        GtfsDatasetRepository gtfsDatasetRepository = mock(GtfsDatasetRepository.class);
        when(gtfsDatasetRepository.getDefaultAgency()).thenReturn(new Agency());

        ShapeProducer shapeProducer = new DefaultShapeProducer(netexDatasetRepository, gtfsDatasetRepository);
        GtfsShape shape = shapeProducer.produce(createTestJourneyPattern());
        Assertions.assertNotNull(shape);
        Assertions.assertEquals(4, shape.getShapePoints().size());

        Assertions.assertEquals(0, shape.getDistanceTravelledToStop(1));
        Assertions.assertEquals(Math.round(serviceLinkLength1), shape.getDistanceTravelledToStop(2));
        Assertions.assertEquals(Math.round(serviceLinkLength1 + serviceLinkLength2), shape.getDistanceTravelledToStop(3));

        Assertions.assertEquals(0, shape.getShapePoints().get(0).getDistTraveled());
        Assertions.assertEquals(Math.round(GeometryUtil.distance(A1, A2)), shape.getShapePoints().get(1).getDistTraveled());
        Assertions.assertEquals(Math.round(GeometryUtil.distance(A1, A2) + GeometryUtil.distance(A2, A3)), shape.getShapePoints().get(2).getDistTraveled());
        Assertions.assertEquals(Math.round(GeometryUtil.distance(A1, A2) + GeometryUtil.distance(A2, A3) + +GeometryUtil.distance(A3, A4)), shape.getShapePoints().get(3).getDistTraveled());


    }

    private static ServiceLink createServiceLink(double... coordinates) {
        List<Double> coordinateList = Arrays.asList(ArrayUtils.toObject(coordinates));

        ServiceLink serviceLink = new ServiceLink();
        Projections_RelStructure projectionsRelStructure = new Projections_RelStructure();
        LinkSequenceProjection linkSequenceProjection = new LinkSequenceProjection();
        LineStringType lineStringType = new LineStringType();
        DirectPositionListType directPositionListType = new DirectPositionListType();
        directPositionListType.withValue(coordinateList);
        lineStringType.setPosList(directPositionListType);
        linkSequenceProjection.setLineString(lineStringType);

        JAXBElement<?> jaxbProjectionVersionStructure = NETEX_FACTORY.createProjection(linkSequenceProjection);

        projectionsRelStructure.getProjectionRefOrProjection().add(0, jaxbProjectionVersionStructure);
        serviceLink.setProjections(projectionsRelStructure);

        return serviceLink;
    }

    private static JourneyPattern createTestJourneyPattern() {

        JourneyPattern journeyPattern = new JourneyPattern();

        PointsInJourneyPattern_RelStructure pointInSequences = new PointsInJourneyPattern_RelStructure();
        StopPointInJourneyPattern pointLinkInSequence1 = new StopPointInJourneyPattern();
        ScheduledStopPointRefStructure scheduledStopPointRefStructure = NETEX_FACTORY.createScheduledStopPointRefStructure();

        JAXBElement<ScheduledStopPointRefStructure> scheduledStopPointRef = NETEX_FACTORY.createScheduledStopPointRef(scheduledStopPointRefStructure);
        pointLinkInSequence1.setScheduledStopPointRef(scheduledStopPointRef);
        pointInSequences.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern().add(0, pointLinkInSequence1);
        PointInLinkSequence_VersionedChildStructure pointLinkInSequence2 = new StopPointInJourneyPattern();
        pointInSequences.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern().add(1, pointLinkInSequence2);
        PointInLinkSequence_VersionedChildStructure pointLinkInSequence3 = new StopPointInJourneyPattern();
        pointInSequences.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern().add(2, pointLinkInSequence3);
        journeyPattern.setPointsInSequence(pointInSequences);

        LinksInJourneyPattern_RelStructure linksInJourneyPatternRelStructure = new LinksInJourneyPattern_RelStructure();

        ServiceLinkInJourneyPattern_VersionedChildStructure serviceLinkInJourneyPatternVersionedChildStructure1 = getServiceLinkInJourneyPattern_versionedChildStructure(BigInteger.ONE, TEST_SERVICE_LINK_1);
        linksInJourneyPatternRelStructure.getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern().add(0, serviceLinkInJourneyPatternVersionedChildStructure1);

        ServiceLinkInJourneyPattern_VersionedChildStructure serviceLinkInJourneyPatternVersionedChildStructure2 = getServiceLinkInJourneyPattern_versionedChildStructure(BigInteger.TWO, TEST_SERVICE_LINK_2);
        linksInJourneyPatternRelStructure.getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern().add(1, serviceLinkInJourneyPatternVersionedChildStructure2);

        journeyPattern.setLinksInSequence(linksInJourneyPatternRelStructure);

        return journeyPattern;
    }

    private static ServiceLinkInJourneyPattern_VersionedChildStructure getServiceLinkInJourneyPattern_versionedChildStructure(BigInteger order, String id) {
        ServiceLinkInJourneyPattern_VersionedChildStructure serviceLinkInJourneyPatternVersionedChildStructure1 = new ServiceLinkInJourneyPattern_VersionedChildStructure();
        serviceLinkInJourneyPatternVersionedChildStructure1.setOrder(order);
        ServiceLinkRefStructure serviceLinkRefStructure1 = new ServiceLinkRefStructure();
        serviceLinkRefStructure1.setRef(id);
        serviceLinkInJourneyPatternVersionedChildStructure1.setServiceLinkRef(serviceLinkRefStructure1);
        return serviceLinkInJourneyPatternVersionedChildStructure1;
    }

}

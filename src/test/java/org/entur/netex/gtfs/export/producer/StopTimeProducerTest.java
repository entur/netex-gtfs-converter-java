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

import org.entur.netex.gtfs.export.model.GtfsShape;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PointInJourneyPatternRefStructure;
import org.rutebanken.netex.model.PointsInJourneyPattern_RelStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;
import org.rutebanken.netex.model.StopPointInJourneyPatternRefStructure;
import org.rutebanken.netex.model.TimetabledPassingTime;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StopTimeProducerTest {

    private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();
    private static final String TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID_1 = "ENT:StopPointInJourneyPattern:1";
    private static final String TEST_SCHEDULED_STOP_POINT_ID_1 = "ENT:ScheduledStopPoint:1";
    private static final String TEST_QUAY_ID = "ENT:QUAY:1";
    private static final LocalTime TEST_ARRIVAL_TIME = LocalTime.of(00, 1);
    private static final String INITIAL_HEADSIGN = "Initial headsign";


    @Test
    void testStopTime() {
        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);
        when(netexDatasetRepository.getQuayIdByScheduledStopPointId(TEST_SCHEDULED_STOP_POINT_ID_1)).thenReturn(TEST_QUAY_ID);

        GtfsDatasetRepository gtfsDatasetRepository = mock(GtfsDatasetRepository.class);
        Stop firstStop = new Stop();
        firstStop.setId(new AgencyAndId("DEFAULT", TEST_QUAY_ID));
        when(gtfsDatasetRepository.getStopById(TEST_QUAY_ID)).thenReturn(firstStop);

        StopTimeProducer stopTimeProducer = new DefaultStopTimeProducer(netexDatasetRepository, gtfsDatasetRepository);

        TimetabledPassingTime timetabledPassingTime = createTestTimetabledPassingTime(TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID_1, TEST_ARRIVAL_TIME);
        JourneyPattern journeyPattern = createTestJourneyPattern(TEST_SCHEDULED_STOP_POINT_ID_1, TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID_1);
        Trip trip = new Trip();
        GtfsShape gtfsShape = new GtfsShape("id", new ArrayList<>(), new ArrayList<>());
        StopTime stopTime = stopTimeProducer.produce(timetabledPassingTime, journeyPattern, trip, gtfsShape, INITIAL_HEADSIGN);

        Assertions.assertNotNull(stopTime);
        // 1 minute past midnight => gtfs time = 60
        Assertions.assertEquals(60, stopTime.getArrivalTime());
        Assertions.assertEquals(INITIAL_HEADSIGN, stopTime.getStopHeadsign());
        Assertions.assertEquals(firstStop, stopTime.getStop());

    }

    @Test
    void testStopTimeWithDayOffset() {
        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);
        when(netexDatasetRepository.getQuayIdByScheduledStopPointId(TEST_SCHEDULED_STOP_POINT_ID_1)).thenReturn(TEST_QUAY_ID);

        GtfsDatasetRepository gtfsDatasetRepository = mock(GtfsDatasetRepository.class);
        Stop firstStop = new Stop();
        firstStop.setId(new AgencyAndId("DEFAULT", TEST_QUAY_ID));
        when(gtfsDatasetRepository.getStopById(TEST_QUAY_ID)).thenReturn(firstStop);

        StopTimeProducer stopTimeProducer = new DefaultStopTimeProducer(netexDatasetRepository, gtfsDatasetRepository);

        TimetabledPassingTime timetabledPassingTime = createTestTimetabledPassingTime(TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID_1, TEST_ARRIVAL_TIME, 1);
        JourneyPattern journeyPattern = createTestJourneyPattern(TEST_SCHEDULED_STOP_POINT_ID_1, TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID_1);
        Trip trip = new Trip();
        GtfsShape gtfsShape = new GtfsShape("id", new ArrayList<>(), new ArrayList<>());
        StopTime stopTime = stopTimeProducer.produce(timetabledPassingTime, journeyPattern, trip, gtfsShape, INITIAL_HEADSIGN);

        Assertions.assertNotNull(stopTime);
        // 1 minute past midnight + 1 day offset => gtfs time = 60 + 60 * 60 * 24
        Assertions.assertEquals(60 + 60 * 60 * 24, stopTime.getArrivalTime());
    }

    @Test
    void testStopTimeNotForBoarding() {
        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);
        when(netexDatasetRepository.getQuayIdByScheduledStopPointId(TEST_SCHEDULED_STOP_POINT_ID_1)).thenReturn(TEST_QUAY_ID);

        GtfsDatasetRepository gtfsDatasetRepository = mock(GtfsDatasetRepository.class);
        Stop firstStop = new Stop();
        firstStop.setId(new AgencyAndId("DEFAULT", TEST_QUAY_ID));
        when(gtfsDatasetRepository.getStopById(TEST_QUAY_ID)).thenReturn(firstStop);

        StopTimeProducer stopTimeProducer = new DefaultStopTimeProducer(netexDatasetRepository, gtfsDatasetRepository);

        TimetabledPassingTime timetabledPassingTime = createTestTimetabledPassingTime(TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID_1, TEST_ARRIVAL_TIME, 1);
        JourneyPattern journeyPattern = createTestJourneyPattern(TEST_SCHEDULED_STOP_POINT_ID_1, TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID_1, Boolean.FALSE, null);
        Trip trip = new Trip();
        GtfsShape gtfsShape = new GtfsShape("id", new ArrayList<>(), new ArrayList<>());
        StopTime stopTime = stopTimeProducer.produce(timetabledPassingTime, journeyPattern, trip, gtfsShape, INITIAL_HEADSIGN);

        Assertions.assertNotNull(stopTime);
        Assertions.assertEquals(StopTimeProducer.PICKUP_AND_DROP_OFF_TYPE_NOT_AVAILABLE, stopTime.getPickupType());
    }

    @Test
    void testStopTimeNotForAlighting() {
        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);
        when(netexDatasetRepository.getQuayIdByScheduledStopPointId(TEST_SCHEDULED_STOP_POINT_ID_1)).thenReturn(TEST_QUAY_ID);

        GtfsDatasetRepository gtfsDatasetRepository = mock(GtfsDatasetRepository.class);
        Stop firstStop = new Stop();
        firstStop.setId(new AgencyAndId("DEFAULT", TEST_QUAY_ID));
        when(gtfsDatasetRepository.getStopById(TEST_QUAY_ID)).thenReturn(firstStop);

        StopTimeProducer stopTimeProducer = new DefaultStopTimeProducer(netexDatasetRepository, gtfsDatasetRepository);

        TimetabledPassingTime timetabledPassingTime = createTestTimetabledPassingTime(TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID_1, TEST_ARRIVAL_TIME, 1);
        JourneyPattern journeyPattern = createTestJourneyPattern(TEST_SCHEDULED_STOP_POINT_ID_1, TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID_1, null, Boolean.FALSE);
        Trip trip = new Trip();
        GtfsShape gtfsShape = new GtfsShape("id", new ArrayList<>(), new ArrayList<>());
        StopTime stopTime = stopTimeProducer.produce(timetabledPassingTime, journeyPattern, trip, gtfsShape, INITIAL_HEADSIGN);

        Assertions.assertNotNull(stopTime);
        Assertions.assertEquals(StopTimeProducer.PICKUP_AND_DROP_OFF_TYPE_NOT_AVAILABLE, stopTime.getDropOffType());
    }

    private JourneyPattern createTestJourneyPattern(String scheduledStopPointId, String stopPointId) {
        return createTestJourneyPattern(scheduledStopPointId, stopPointId, null, null);
    }

    private JourneyPattern createTestJourneyPattern(String scheduledStopPointId, String stopPointId, Boolean isForBoarding, Boolean isForAlighting) {
        JourneyPattern journeyPattern = NETEX_FACTORY.createJourneyPattern();
        StopPointInJourneyPattern stopPointInJourneyPattern = NETEX_FACTORY.createStopPointInJourneyPattern();
        stopPointInJourneyPattern.setId(stopPointId);
        ScheduledStopPointRefStructure scheduledStopPointRefStructure = NETEX_FACTORY.createScheduledStopPointRefStructure();
        scheduledStopPointRefStructure.setRef(scheduledStopPointId);
        JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPointRef = NETEX_FACTORY.createScheduledStopPointRef(scheduledStopPointRefStructure);
        stopPointInJourneyPattern.setScheduledStopPointRef(scheduledStopPointRef);
        stopPointInJourneyPattern.setOrder(BigInteger.valueOf(1));
        stopPointInJourneyPattern.setForBoarding(isForBoarding);
        stopPointInJourneyPattern.setForAlighting(isForAlighting);
        PointsInJourneyPattern_RelStructure pointsInSequence = NETEX_FACTORY.createPointsInJourneyPattern_RelStructure();
        pointsInSequence.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern().add(stopPointInJourneyPattern);
        journeyPattern.setPointsInSequence(pointsInSequence);
        return journeyPattern;
    }

    private TimetabledPassingTime createTestTimetabledPassingTime(String stopPointInJourneyPtternRef, LocalTime arrivalTime) {
        return createTestTimetabledPassingTime(stopPointInJourneyPtternRef, arrivalTime, 0);
    }

    private TimetabledPassingTime createTestTimetabledPassingTime(String stopPointInJourneyPtternRef, LocalTime arrivalTime, int dayOffset) {
        TimetabledPassingTime timetabledPassingTime = NETEX_FACTORY.createTimetabledPassingTime();
        timetabledPassingTime.setArrivalTime(arrivalTime);
        if (dayOffset != 0) {
            timetabledPassingTime.setArrivalDayOffset(BigInteger.valueOf(dayOffset));
        }
        StopPointInJourneyPatternRefStructure stopPointInJourneyPatternRefStructure = NETEX_FACTORY.createStopPointInJourneyPatternRefStructure();
        stopPointInJourneyPatternRefStructure.setRef(stopPointInJourneyPtternRef);
        JAXBElement<? extends PointInJourneyPatternRefStructure> pointInJourneyPatternRef = NETEX_FACTORY.createStopPointInJourneyPatternRef(stopPointInJourneyPatternRefStructure);
        timetabledPassingTime.setPointInJourneyPatternRef(pointInJourneyPatternRef);
        return timetabledPassingTime;
    }
}

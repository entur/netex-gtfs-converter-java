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

import static org.entur.netex.gtfs.export.util.GtfsUtil.toGtfsTimeWithDayOffset;

import org.entur.netex.gtfs.export.exception.GtfsExportException;
import org.entur.netex.gtfs.export.model.GtfsShape;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.util.DestinationDisplayUtil;
import org.entur.netex.gtfs.export.util.StopUtil;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.StopPointInJourneyPattern;
import org.rutebanken.netex.model.TimetabledPassingTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultStopTimeProducer implements StopTimeProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    DefaultStopTimeProducer.class
  );

  private final NetexDatasetRepository netexDatasetRepository;
  private final GtfsDatasetRepository gtfsDatasetRepository;

  public DefaultStopTimeProducer(
    NetexDatasetRepository netexDatasetRepository,
    GtfsDatasetRepository gtfsDatasetRepository
  ) {
    this.netexDatasetRepository = netexDatasetRepository;
    this.gtfsDatasetRepository = gtfsDatasetRepository;
  }

  @Override
  public StopTime produce(
    TimetabledPassingTime timetabledPassingTime,
    JourneyPattern journeyPattern,
    Trip trip,
    GtfsShape gtfsShape,
    String currentHeadSign
  ) {
    StopTime stopTime = new StopTime();

    // trip
    stopTime.setTrip(trip);

    // stop
    String pointInJourneyPatternRef = timetabledPassingTime
      .getPointInJourneyPatternRef()
      .getValue()
      .getRef();
    StopPointInJourneyPattern stopPointInSequence =
      (StopPointInJourneyPattern) journeyPattern
        .getPointsInSequence()
        .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern()
        .stream()
        .filter(stopPointInJourneyPattern ->
          stopPointInJourneyPattern.getId().equals(pointInJourneyPatternRef)
        )
        .findFirst()
        .orElseThrow(() ->
          new GtfsExportException(
            "Could not find StopPointInJourneyPattern with id " +
            pointInJourneyPatternRef
          )
        );
    int stopSequence = stopPointInSequence.getOrder().intValueExact();
    stopTime.setStopSequence(stopSequence);
    String scheduledStopPointId = stopPointInSequence
      .getScheduledStopPointRef()
      .getValue()
      .getRef();
    Stop stop = StopUtil.getGtfsStopFromScheduledStopPointId(
      scheduledStopPointId,
      netexDatasetRepository,
      gtfsDatasetRepository
    );
    stopTime.setStop(stop);

    // arrival time
    if (timetabledPassingTime.getArrivalTime() != null) {
      int dayOffset = timetabledPassingTime.getArrivalDayOffset() == null
        ? 0
        : timetabledPassingTime.getArrivalDayOffset().intValueExact();
      stopTime.setArrivalTime(
        toGtfsTimeWithDayOffset(
          timetabledPassingTime.getArrivalTime(),
          dayOffset
        )
      );

      if (timetabledPassingTime.getDepartureTime() == null) {
        stopTime.setDepartureTime(stopTime.getArrivalTime());
      }
    }

    // departure time
    if (timetabledPassingTime.getDepartureTime() != null) {
      int dayOffset = timetabledPassingTime.getDepartureDayOffset() == null
        ? 0
        : timetabledPassingTime.getDepartureDayOffset().intValueExact();
      stopTime.setDepartureTime(
        toGtfsTimeWithDayOffset(
          timetabledPassingTime.getDepartureTime(),
          dayOffset
        )
      );

      if (timetabledPassingTime.getArrivalTime() == null) {
        stopTime.setArrivalTime(stopTime.getDepartureTime());
      }
    }

    // destination display = stop head sign
    // the head sign is by default the destination display set on the current stop
    // it can be ignored if it is the same as the trip head sign
    String stopHeadSignOnCurrentStop = null;
    if (stopPointInSequence.getDestinationDisplayRef() != null) {
      DestinationDisplay destinationDisplay =
        netexDatasetRepository.getDestinationDisplayById(
          stopPointInSequence.getDestinationDisplayRef().getRef()
        );
      stopHeadSignOnCurrentStop =
        DestinationDisplayUtil.getHeadSignFromDestinationDisplay(
          destinationDisplay,
          netexDatasetRepository
        );
      if (
        stopHeadSignOnCurrentStop != null &&
        stopHeadSignOnCurrentStop.equals(trip.getTripHeadsign())
      ) {
        stopHeadSignOnCurrentStop = null;
      }
    }
    // otherwise the head sign from the previous stop is used
    // in GTFS the head sign must be explicitly set from the first stop where the head sign has changed to the last stop the change applies.
    if (stopHeadSignOnCurrentStop == null) {
      stopHeadSignOnCurrentStop = currentHeadSign;
    }
    stopTime.setStopHeadsign(stopHeadSignOnCurrentStop);

    // boarding = pickup
    if (Boolean.FALSE.equals(stopPointInSequence.isForBoarding())) {
      stopTime.setPickupType(
        StopTimeProducer.PICKUP_AND_DROP_OFF_TYPE_NOT_AVAILABLE
      );
    }

    // alighting = drop off
    if (Boolean.FALSE.equals(stopPointInSequence.isForAlighting())) {
      stopTime.setDropOffType(
        StopTimeProducer.PICKUP_AND_DROP_OFF_TYPE_NOT_AVAILABLE
      );
    }

    // pickup and stop on request override the values set in isForBoarding and isForAlighting
    if (Boolean.TRUE.equals(stopPointInSequence.isRequestStop())) {
      stopTime.setPickupType(
        StopTimeProducer.PICKUP_AND_DROP_OFF_TYPE_MUST_COORDINATE_WITH_DRIVER
      );
      stopTime.setDropOffType(
        StopTimeProducer.PICKUP_AND_DROP_OFF_TYPE_MUST_COORDINATE_WITH_DRIVER
      );
    }

    // distance travelled
    if (trip.getShapeId() == null) {
      LOGGER.trace("skipping distance travelled for trip {}", trip.getId());
    } else {
      stopTime.setShapeDistTraveled(
        gtfsShape.getDistanceTravelledToStop(stopSequence)
      );
    }

    return stopTime;
  }
}

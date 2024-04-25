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

package org.entur.netex.gtfs.export;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.entur.netex.gtfs.export.loader.DefaultNetexDatasetLoader;
import org.entur.netex.gtfs.export.loader.NetexDatasetLoader;
import org.entur.netex.gtfs.export.model.GtfsService;
import org.entur.netex.gtfs.export.model.GtfsShape;
import org.entur.netex.gtfs.export.model.ServiceCalendarPeriod;
import org.entur.netex.gtfs.export.producer.AgencyProducer;
import org.entur.netex.gtfs.export.producer.DefaultAgencyProducer;
import org.entur.netex.gtfs.export.producer.DefaultGtfsServiceRepository;
import org.entur.netex.gtfs.export.producer.DefaultRouteProducer;
import org.entur.netex.gtfs.export.producer.DefaultServiceCalendarDateProducer;
import org.entur.netex.gtfs.export.producer.DefaultServiceCalendarProducer;
import org.entur.netex.gtfs.export.producer.DefaultShapeProducer;
import org.entur.netex.gtfs.export.producer.DefaultStopProducer;
import org.entur.netex.gtfs.export.producer.DefaultStopTimeProducer;
import org.entur.netex.gtfs.export.producer.DefaultTransferProducer;
import org.entur.netex.gtfs.export.producer.DefaultTripProducer;
import org.entur.netex.gtfs.export.producer.FeedInfoProducer;
import org.entur.netex.gtfs.export.producer.GtfsServiceRepository;
import org.entur.netex.gtfs.export.producer.RouteProducer;
import org.entur.netex.gtfs.export.producer.ServiceCalendarDateProducer;
import org.entur.netex.gtfs.export.producer.ServiceCalendarProducer;
import org.entur.netex.gtfs.export.producer.ShapeProducer;
import org.entur.netex.gtfs.export.producer.StopProducer;
import org.entur.netex.gtfs.export.producer.StopTimeProducer;
import org.entur.netex.gtfs.export.producer.TransferProducer;
import org.entur.netex.gtfs.export.producer.TripProducer;
import org.entur.netex.gtfs.export.repository.DefaultGtfsRepository;
import org.entur.netex.gtfs.export.repository.DefaultNetexDatasetRepository;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.stop.StopAreaRepository;
import org.entur.netex.gtfs.export.util.DestinationDisplayUtil;
import org.entur.netex.gtfs.export.util.ServiceAlterationChecker;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.StopPointInJourneyPattern;
import org.rutebanken.netex.model.TimetabledPassingTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGtfsExporter implements GtfsExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    DefaultGtfsExporter.class
  );

  private final String codespace;

  private final NetexDatasetRepository netexDatasetRepository;
  private final GtfsDatasetRepository gtfsDatasetRepository;
  private final GtfsServiceRepository gtfsServiceRepository;
  private final StopAreaRepository stopAreaRepository;

  private TransferProducer transferProducer;
  private AgencyProducer agencyProducer;
  private FeedInfoProducer feedInfoProducer;
  private RouteProducer routeProducer;
  private ShapeProducer shapeProducer;
  private TripProducer tripProducer;
  private StopTimeProducer stopTimeProducer;
  private ServiceCalendarDateProducer serviceCalendarDateProducer;
  private ServiceCalendarProducer serviceCalendarProducer;
  private StopProducer stopProducer;
  private NetexDatasetLoader netexDatasetLoader;
  private final ServiceAlterationChecker serviceAlterationChecker;

  /**
   * Create a GTFS exporter for a given codespace.
   *
   * @param codespace          the codespace of the exported dataset. This is the codespace of the data provider who owns the timetable dataset.
   * @param stopAreaRepository the stop area repository.
   */

  public DefaultGtfsExporter(
    String codespace,
    StopAreaRepository stopAreaRepository
  ) {
    this(codespace, stopAreaRepository, false);
  }

  /**
   * Create a GTFS exporter not tied to a specific codespace.
   * This can be used to export only stops, without timetable data.
   *
   * @param stopAreaRepository the stop area repository.
   */
  public DefaultGtfsExporter(StopAreaRepository stopAreaRepository) {
    this(null, stopAreaRepository);
  }

  public DefaultGtfsExporter(
    String codespace,
    StopAreaRepository stopAreaRepository,
    boolean generateStaySeatedTransfer
  ) {
    this.codespace = codespace;

    this.netexDatasetLoader = new DefaultNetexDatasetLoader();

    this.stopAreaRepository = stopAreaRepository;
    this.gtfsDatasetRepository = new DefaultGtfsRepository();
    this.netexDatasetRepository = new DefaultNetexDatasetRepository();
    this.gtfsServiceRepository =
      new DefaultGtfsServiceRepository(codespace, netexDatasetRepository);
    this.serviceAlterationChecker =
      new ServiceAlterationChecker(netexDatasetRepository);

    this.transferProducer =
      new DefaultTransferProducer(
        netexDatasetRepository,
        gtfsDatasetRepository,
        generateStaySeatedTransfer
      );

    this.agencyProducer = new DefaultAgencyProducer(netexDatasetRepository);
    this.routeProducer =
      new DefaultRouteProducer(netexDatasetRepository, gtfsDatasetRepository);
    this.shapeProducer =
      new DefaultShapeProducer(netexDatasetRepository, gtfsDatasetRepository);
    this.tripProducer =
      new DefaultTripProducer(
        netexDatasetRepository,
        gtfsDatasetRepository,
        gtfsServiceRepository
      );
    this.stopTimeProducer =
      new DefaultStopTimeProducer(
        netexDatasetRepository,
        gtfsDatasetRepository
      );
    this.serviceCalendarDateProducer =
      new DefaultServiceCalendarDateProducer(gtfsDatasetRepository);
    this.serviceCalendarProducer =
      new DefaultServiceCalendarProducer(gtfsDatasetRepository);
    this.stopProducer =
      new DefaultStopProducer(stopAreaRepository, gtfsDatasetRepository);
  }

  @Override
  public InputStream convertTimetablesToGtfs(
    InputStream netexTimetableDataset
  ) {
    if (codespace == null) {
      throw new IllegalStateException(
        "Missing required codespace for timetable data export"
      );
    }
    loadNetex(netexTimetableDataset);
    convertNetexToGtfs();
    return gtfsDatasetRepository.writeGtfs();
  }

  @Override
  public InputStream convertStopsToGtfs() {
    convertStops(false);
    addFeedInfo();
    return gtfsDatasetRepository.writeGtfs();
  }

  private void loadNetex(InputStream netexTimetableDataset) {
    LOGGER.info(
      "Importing NeTEx Timetable dataset for codespace {}",
      codespace
    );
    netexDatasetLoader.load(netexTimetableDataset, netexDatasetRepository);
    LOGGER.info("Imported NeTEx Timetable dataset for codespace {}", codespace);
  }

  private void convertNetexToGtfs() {
    LOGGER.info("Converting NeTEx to GTFS for codespace {}", codespace);
    // create agencies only for authorities that are effectively referenced from a NeTex line
    netexDatasetRepository
      .getLines()
      .stream()
      .map(netexDatasetRepository::getAuthorityIdForLine)
      .distinct()
      .map(netexDatasetRepository::getAuthorityById)
      .map(agencyProducer::produce)
      .forEach(gtfsDatasetRepository::saveEntity);

    convertStops(true);
    convertRoutes();
    convertServices();
    convertTransfers();
    addFeedInfo();
    LOGGER.info("Converted NeTEx to GTFS for codespace {}", codespace);
  }

  protected void addFeedInfo() {
    if (feedInfoProducer != null) {
      FeedInfo feedInfo = feedInfoProducer.produceFeedInfo();
      if (feedInfo != null) {
        gtfsDatasetRepository.saveEntity(feedInfo);
      }
    }
  }

  protected void convertRoutes() {
    for (Line netexLine : netexDatasetRepository.getLines()) {
      Route gtfsRoute = routeProducer.produce(netexLine);
      gtfsDatasetRepository.saveEntity(gtfsRoute);
      for (org.rutebanken.netex.model.Route netexRoute : netexDatasetRepository.getRoutesByLine(
        netexLine
      )) {
        for (JourneyPattern journeyPattern : netexDatasetRepository.getJourneyPatternsByRoute(
          netexRoute
        )) {
          GtfsShape gtfsShape = shapeProducer.produce(journeyPattern);
          AgencyAndId shapeId = null;
          if (gtfsShape != null && !gtfsShape.getShapePoints().isEmpty()) {
            gtfsShape
              .getShapePoints()
              .forEach(gtfsDatasetRepository::saveEntity);
            shapeId = new AgencyAndId();
            shapeId.setAgencyId(
              gtfsDatasetRepository.getDefaultAgency().getId()
            );
            shapeId.setId(gtfsShape.getId());
          }

          DestinationDisplay initialDestinationDisplay =
            DestinationDisplayUtil.getInitialDestinationDisplay(
              journeyPattern,
              netexDatasetRepository
            );

          for (ServiceJourney serviceJourney : netexDatasetRepository.getServiceJourneysByJourneyPattern(
            journeyPattern
          )) {
            Trip trip = tripProducer.produce(
              serviceJourney,
              netexRoute,
              gtfsRoute,
              shapeId,
              initialDestinationDisplay
            );
            if (trip != null) {
              gtfsDatasetRepository.saveEntity(trip);
              // the head sign set on a given stop depends on the one set on the previous stop
              // i.e. it must be repeated from one stop to the next unless there is an explicit change.
              String currentHeadSign = null;
              for (TimetabledPassingTime timetabledPassingTime : serviceJourney
                .getPassingTimes()
                .getTimetabledPassingTime()) {
                StopTime stopTime = stopTimeProducer.produce(
                  timetabledPassingTime,
                  journeyPattern,
                  trip,
                  gtfsShape,
                  currentHeadSign
                );
                gtfsDatasetRepository.saveEntity(stopTime);
                currentHeadSign = stopTime.getStopHeadsign();
              }
            }
          }
        }
      }
    }
  }

  protected void convertServices() {
    for (GtfsService gtfsService : gtfsServiceRepository.getAllServices()) {
      ServiceCalendarPeriod serviceCalendarPeriod =
        gtfsService.getServiceCalendarPeriod();
      if (serviceCalendarPeriod != null) {
        ServiceCalendar serviceCalendar = serviceCalendarProducer.produce(
          gtfsService.getId(),
          serviceCalendarPeriod.getStartDate(),
          serviceCalendarPeriod.getEndDate(),
          serviceCalendarPeriod.getDaysOfWeek()
        );
        gtfsDatasetRepository.saveEntity(serviceCalendar);
      }
      for (LocalDateTime includedDate : gtfsService.getIncludedDates()) {
        gtfsDatasetRepository.saveEntity(
          serviceCalendarDateProducer.produce(
            gtfsService.getId(),
            includedDate,
            true
          )
        );
      }
      for (LocalDateTime excludedDate : gtfsService.getExcludedDates()) {
        gtfsDatasetRepository.saveEntity(
          serviceCalendarDateProducer.produce(
            gtfsService.getId(),
            excludedDate,
            false
          )
        );
      }
    }
  }

  protected void convertTransfers() {
    netexDatasetRepository
      .getServiceJourneyInterchanges()
      .stream()
      .filter(this::isValidServiceJourneyInterchange)
      .map(transferProducer::produce)
      .forEach(gtfsDatasetRepository::saveEntity);
  }

  protected void convertStops(boolean exportOnlyUsedStops) {
    Set<String> allQuaysId;

    if (exportOnlyUsedStops) {
      // Retrieve all quays referenced by valid ServiceJourneys
      // This excludes quays referenced by cancelled or replaced service journeys
      // and quays referenced only as route points or in dead runs
      allQuaysId =
        netexDatasetRepository
          .getServiceJourneys()
          .stream()
          .filter(
            Predicate.not(serviceAlterationChecker::isReplacedOrCancelled)
          )
          .map(serviceJourney ->
            serviceJourney.getJourneyPatternRef().getValue().getRef()
          )
          .distinct()
          .map(netexDatasetRepository::getJourneyPatternById)
          .map(journeyPattern ->
            journeyPattern
              .getPointsInSequence()
              .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern()
          )
          .flatMap(Collection::stream)
          .map(stopPointInJourneyPattern ->
            (
              (StopPointInJourneyPattern) stopPointInJourneyPattern
            ).getScheduledStopPointRef()
              .getValue()
              .getRef()
          )
          .distinct()
          .filter(Predicate.not(this::isFlexibleScheduledStopPoint))
          .map(netexDatasetRepository::getQuayIdByScheduledStopPointId)
          .collect(Collectors.toSet());
    } else {
      allQuaysId =
        stopAreaRepository
          .getAllQuays()
          .stream()
          .map(Quay::getId)
          .collect(Collectors.toSet());
    }

    // Persist the quays
    allQuaysId
      .stream()
      .map(stopAreaRepository::getQuayById)
      .map(stopProducer::produceStopFromQuay)
      .forEach(gtfsDatasetRepository::saveEntity);

    // Retrieve and persist all the stop places that contain the quays
    allQuaysId
      .stream()
      .map(stopAreaRepository::getStopPlaceByQuayId)
      .distinct()
      .map(stopProducer::produceStopFromStopPlace)
      .forEach(gtfsDatasetRepository::saveEntity);
  }

  /**
   * A service journey is valid if the referenced ServiceJourneys are neither replaced nor cancelled.
   *
   * @param serviceJourneyInterchange the ServiceJourneyInterchange to check.
   * @return true if the referenced ServiceJourneys are neither replaced nor cancelled.
   */
  private boolean isValidServiceJourneyInterchange(
    ServiceJourneyInterchange serviceJourneyInterchange
  ) {
    ServiceJourney fromServiceJourney =
      netexDatasetRepository.getServiceJourneyById(
        serviceJourneyInterchange.getFromJourneyRef().getRef()
      );
    ServiceJourney toServiceJourney =
      netexDatasetRepository.getServiceJourneyById(
        serviceJourneyInterchange.getToJourneyRef().getRef()
      );
    boolean hasValidReferences =
      fromServiceJourney != null && toServiceJourney != null;
    if (!hasValidReferences) {
      LOGGER.warn(
        "Filtering ServiceJourneyInterchange {} with invalid references.",
        serviceJourneyInterchange.getId()
      );
      return false;
    }
    boolean isActive =
      !serviceAlterationChecker.isReplacedOrCancelled(fromServiceJourney) &&
      !serviceAlterationChecker.isReplacedOrCancelled(toServiceJourney);
    if (!isActive) {
      LOGGER.info(
        "Filtering cancelled or replaced ServiceJourneyInterchange {}",
        serviceJourneyInterchange.getId()
      );
    }
    return isActive;
  }

  private boolean isFlexibleScheduledStopPoint(String scheduledStopPointId) {
    String flexibleStopPlaceId =
      netexDatasetRepository.getFlexibleStopPlaceIdByScheduledStopPointId(
        scheduledStopPointId
      );
    if (flexibleStopPlaceId != null) {
      LOGGER.info(
        "Ignoring scheduled stop point {} referring to flexible stop place {}",
        scheduledStopPointId,
        flexibleStopPlaceId
      );
      return true;
    }
    return false;
  }

  protected final String getCodespace() {
    return codespace;
  }

  protected final NetexDatasetRepository getNetexDatasetRepository() {
    return netexDatasetRepository;
  }

  protected final GtfsDatasetRepository getGtfsDatasetRepository() {
    return gtfsDatasetRepository;
  }

  protected final GtfsServiceRepository getGtfsServiceRepository() {
    return gtfsServiceRepository;
  }

  protected final StopAreaRepository getStopAreaRepository() {
    return stopAreaRepository;
  }

  protected final void setNetexDatasetLoader(
    NetexDatasetLoader netexDatasetLoader
  ) {
    this.netexDatasetLoader = netexDatasetLoader;
  }

  protected final void setTransferProducer(TransferProducer transferProducer) {
    this.transferProducer = transferProducer;
  }

  protected final void setAgencyProducer(AgencyProducer agencyProducer) {
    this.agencyProducer = agencyProducer;
  }

  protected final void setFeedInfoProducer(FeedInfoProducer feedInfoProducer) {
    this.feedInfoProducer = feedInfoProducer;
  }

  protected final void setRouteProducer(RouteProducer routeProducer) {
    this.routeProducer = routeProducer;
  }

  protected final void setShapeProducer(ShapeProducer shapeProducer) {
    this.shapeProducer = shapeProducer;
  }

  protected final void setTripProducer(TripProducer tripProducer) {
    this.tripProducer = tripProducer;
  }

  protected final void setStopTimeProducer(StopTimeProducer stopTimeProducer) {
    this.stopTimeProducer = stopTimeProducer;
  }

  protected final void setServiceCalendarDateProducer(
    ServiceCalendarDateProducer serviceCalendarDateProducer
  ) {
    this.serviceCalendarDateProducer = serviceCalendarDateProducer;
  }

  protected final void setServiceCalendarProducer(
    ServiceCalendarProducer serviceCalendarProducer
  ) {
    this.serviceCalendarProducer = serviceCalendarProducer;
  }

  protected final void setStopProducer(StopProducer stopProducer) {
    this.stopProducer = stopProducer;
  }
}

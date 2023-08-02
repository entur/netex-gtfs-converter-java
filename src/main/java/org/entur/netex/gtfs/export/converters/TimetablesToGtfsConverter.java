package org.entur.netex.gtfs.export.converters;

import org.entur.netex.gtfs.export.model.GtfsService;
import org.entur.netex.gtfs.export.model.GtfsShape;
import org.entur.netex.gtfs.export.model.ServiceCalendarPeriod;
import org.entur.netex.gtfs.export.producer.*;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.stop.StopAreaRepository;
import org.entur.netex.gtfs.export.util.DestinationDisplayUtil;
import org.entur.netex.gtfs.export.util.ServiceAlterationChecker;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.*;
import org.rutebanken.netex.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TimetablesToGtfsConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimetablesToGtfsConverter.class);

    private final NetexDatasetRepository netexDatasetRepository;
    private final GtfsDatasetRepository gtfsDatasetRepository;
    private final StopAreaRepository stopAreaRepository;
    private final GtfsServiceRepository gtfsServiceRepository;

    private final ServiceAlterationChecker serviceAlterationChecker;

    private final StopProducer stopProducer;
    private final AgencyProducer agencyProducer;
    private final RouteProducer routeProducer;
    private final ShapeProducer shapeProducer;
    private final TripProducer tripProducer;
    private final StopTimeProducer stopTimeProducer;
    private final ServiceCalendarProducer serviceCalendarProducer;
    private final ServiceCalendarDateProducer serviceCalendarDateProducer;
    private final TransferProducer transferProducer;

    public TimetablesToGtfsConverter(NetexDatasetRepository netexDatasetRepository,
                                     GtfsDatasetRepository gtfsDatasetRepository,
                                     StopAreaRepository stopAreaRepository,
                                     GtfsServiceRepository gtfsServiceRepository,
                                     boolean generateStaySeatedTransfer) {

        this.netexDatasetRepository = netexDatasetRepository;
        this.gtfsDatasetRepository = gtfsDatasetRepository;
        this.stopAreaRepository = stopAreaRepository;
        this.gtfsServiceRepository = gtfsServiceRepository;

        this.serviceAlterationChecker = new ServiceAlterationChecker(netexDatasetRepository);
        this.transferProducer = new DefaultTransferProducer(netexDatasetRepository, gtfsDatasetRepository, generateStaySeatedTransfer);
        this.agencyProducer = new DefaultAgencyProducer(netexDatasetRepository);
        this.routeProducer = new DefaultRouteProducer(netexDatasetRepository, gtfsDatasetRepository);
        this.shapeProducer = new DefaultShapeProducer(netexDatasetRepository, gtfsDatasetRepository);
        this.tripProducer = new DefaultTripProducer(netexDatasetRepository, gtfsDatasetRepository, gtfsServiceRepository);
        this.stopTimeProducer = new DefaultStopTimeProducer(netexDatasetRepository, gtfsDatasetRepository);
        this.serviceCalendarDateProducer = new DefaultServiceCalendarDateProducer(gtfsDatasetRepository);
        this.serviceCalendarProducer = new DefaultServiceCalendarProducer(gtfsDatasetRepository);
        this.stopProducer = new DefaultStopProducer(stopAreaRepository, gtfsDatasetRepository);
    }

    public void convert() {
        LOGGER.info("Converting NeTEx to GTFS");
        // create agencies only for authorities that are effectively referenced from a NeTex line
        netexDatasetRepository.getLines()
                .stream()
                .map(netexDatasetRepository::getAuthorityIdForLine)
                .distinct()
                .map(netexDatasetRepository::getAuthorityById)
                .map(agencyProducer::produce)
                .forEach(gtfsDatasetRepository::saveEntity);

        convertStops();
        convertRoutes();
        convertServices();
        convertTransfers();
    }

    public void convertStops() {

        // Retrieve all quays referenced by valid ServiceJourneys
        // This excludes quays referenced by cancelled or replaced service journeys
        // and quays referenced only as route points or in dead runs
        LOGGER.info("Converting stops");

        Set<String> allQuaysId = netexDatasetRepository.getServiceJourneys()
                .stream()
                .filter(Predicate.not(serviceAlterationChecker::isReplacedOrCancelled))
                .map(serviceJourney -> serviceJourney.getJourneyPatternRef().getValue().getRef())
                .distinct()
                .map(netexDatasetRepository::getJourneyPatternById)
                .map(journeyPattern ->
                        journeyPattern
                                .getPointsInSequence()
                                .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern())
                .flatMap(Collection::stream)
                .map(stopPointInJourneyPattern ->
                        ((StopPointInJourneyPattern) stopPointInJourneyPattern)
                                .getScheduledStopPointRef()
                                .getValue()
                                .getRef()
                )
                .distinct()
                .filter(Predicate.not(this::isFlexibleScheduledStopPoint))
                .map(netexDatasetRepository::getQuayIdByScheduledStopPointId)
                .collect(Collectors.toSet());

        // Persist the quays
        allQuaysId.stream().map(stopAreaRepository::getQuayById)
                .map(stopProducer::produceStopFromQuay)
                .forEach(gtfsDatasetRepository::saveEntity);

        // Retrieve and persist all the stop places that contain the quays
        allQuaysId.stream()
                .map(stopAreaRepository::getStopPlaceByQuayId)
                .distinct()
                .map(stopProducer::produceStopFromStopPlace)
                .forEach(gtfsDatasetRepository::saveEntity);
    }

    public void convertRoutes() {

        LOGGER.info("Converting routes, shapes, trip and stop times");

        for (Line netexLine : netexDatasetRepository.getLines()) {
            Route gtfsRoute = routeProducer.produce(netexLine);
            gtfsDatasetRepository.saveEntity(gtfsRoute);
            for (org.rutebanken.netex.model.Route netexRoute : netexDatasetRepository.getRoutesByLine(netexLine)) {
                for (JourneyPattern journeyPattern : netexDatasetRepository.getJourneyPatternsByRoute(netexRoute)) {
                    GtfsShape gtfsShape = shapeProducer.produce(journeyPattern);
                    AgencyAndId shapeId = null;
                    if (gtfsShape != null && !gtfsShape.getShapePoints().isEmpty()) {
                        gtfsShape.getShapePoints().forEach(gtfsDatasetRepository::saveEntity);
                        shapeId = new AgencyAndId();
                        shapeId.setAgencyId(gtfsDatasetRepository.getDefaultAgency().getId());
                        shapeId.setId(gtfsShape.getId());
                    }

                    DestinationDisplay initialDestinationDisplay = DestinationDisplayUtil.getInitialDestinationDisplay(journeyPattern, netexDatasetRepository);

                    for (ServiceJourney serviceJourney : netexDatasetRepository.getServiceJourneysByJourneyPattern(journeyPattern)) {
                        Trip trip = tripProducer.produce(serviceJourney, netexRoute, gtfsRoute, shapeId, initialDestinationDisplay);
                        if (trip != null) {
                            gtfsDatasetRepository.saveEntity(trip);
                            // the head sign set on a given stop depends on the one set on the previous stop
                            // i.e. it must be repeated from one stop to the next unless there is an explicit change.
                            String currentHeadSign = null;
                            for (TimetabledPassingTime timetabledPassingTime : serviceJourney.getPassingTimes().getTimetabledPassingTime()) {
                                StopTime stopTime = stopTimeProducer.produce(timetabledPassingTime, journeyPattern, trip, gtfsShape, currentHeadSign);
                                gtfsDatasetRepository.saveEntity(stopTime);
                                currentHeadSign = stopTime.getStopHeadsign();
                            }
                        }
                    }
                }
            }
        }
    }

    public void convertServices() {

        LOGGER.info("Converting services");
        for (GtfsService gtfsService : gtfsServiceRepository.getAllServices()) {
            ServiceCalendarPeriod serviceCalendarPeriod = gtfsService.getServiceCalendarPeriod();
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
                gtfsDatasetRepository.saveEntity(serviceCalendarDateProducer.produce(gtfsService.getId(), includedDate, true));
            }
            for (LocalDateTime excludedDate : gtfsService.getExcludedDates()) {
                gtfsDatasetRepository.saveEntity(serviceCalendarDateProducer.produce(gtfsService.getId(), excludedDate, false));
            }
        }
    }

    public void convertTransfers() {
        LOGGER.info("Converting transfers");
        netexDatasetRepository.getServiceJourneyInterchanges()
                .stream()
                .filter(this::isValidServiceJourneyInterchange)
                .map(transferProducer::produce)
                .forEach(gtfsDatasetRepository::saveEntity);
    }

    /**
     * A service journey is valid if the referenced ServiceJourneys are neither replaced nor cancelled.
     *
     * @param serviceJourneyInterchange the ServiceJourneyInterchange to check.
     * @return true if the referenced ServiceJourneys are neither replaced nor cancelled.
     */
    public boolean isValidServiceJourneyInterchange(ServiceJourneyInterchange serviceJourneyInterchange) {
        ServiceJourney fromServiceJourney = netexDatasetRepository.getServiceJourneyById(serviceJourneyInterchange.getFromJourneyRef().getRef());
        ServiceJourney toServiceJourney = netexDatasetRepository.getServiceJourneyById(serviceJourneyInterchange.getToJourneyRef().getRef());
        boolean hasValidReferences = fromServiceJourney != null && toServiceJourney != null;
        if (!hasValidReferences) {
            LOGGER.warn("Filtering ServiceJourneyInterchange {} with invalid references.", serviceJourneyInterchange.getId());
            return false;
        }
        boolean isActive = !serviceAlterationChecker.isReplacedOrCancelled(fromServiceJourney) && !serviceAlterationChecker.isReplacedOrCancelled(toServiceJourney);
        if (!isActive) {
            LOGGER.info("Filtering cancelled or replaced ServiceJourneyInterchange {}", serviceJourneyInterchange.getId());
        }
        return isActive;
    }

    private boolean isFlexibleScheduledStopPoint(String scheduledStopPointId) {
        String flexibleStopPlaceId = netexDatasetRepository.getFlexibleStopPlaceIdByScheduledStopPointId(scheduledStopPointId);
        if (flexibleStopPlaceId != null) {
            LOGGER.info("Ignoring scheduled stop point {} referring to flexible stop place {}", scheduledStopPointId, flexibleStopPlaceId);
            return true;
        }
        return false;
    }
}
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

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.util.DestinationDisplayUtil;
import org.entur.netex.gtfs.export.util.ServiceAlterationChecker;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DirectionTypeEnumeration;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.ServiceJourney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produce a GTFS Trip or null if the service journey does not correspond to a valid TFS Trip
 * In particular ServiceJourney having a ServiceAlteration=cancelled or ServiceAlteration=replaced are not valid GTFS Trip.
 */
public class DefaultTripProducer implements TripProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    DefaultTripProducer.class
  );

  private final Agency agency;
  private final GtfsServiceRepository gtfsServiceRepository;
  private final NetexDatasetRepository netexDatasetRepository;
  private final ServiceAlterationChecker serviceAlterationChecker;

  public DefaultTripProducer(
    NetexDatasetRepository netexDatasetRepository,
    GtfsDatasetRepository gtfsDatasetRepository,
    GtfsServiceRepository gtfsServiceRepository
  ) {
    this.agency = gtfsDatasetRepository.getDefaultAgency();
    this.gtfsServiceRepository = gtfsServiceRepository;
    this.netexDatasetRepository = netexDatasetRepository;
    this.serviceAlterationChecker =
      new ServiceAlterationChecker(netexDatasetRepository);
  }

  @Override
  public Trip produce(
    ServiceJourney serviceJourney,
    Route netexRoute,
    org.onebusaway.gtfs.model.Route gtfsRoute,
    AgencyAndId shapeId,
    DestinationDisplay initialDestinationDisplay
  ) {
    // Cancelled or replaced service journeys are not valid GTFS trips.
    if (serviceAlterationChecker.isReplacedOrCancelled(serviceJourney)) {
      return null;
    }

    String tripId = serviceJourney.getId();

    AgencyAndId tripAgencyAndId = new AgencyAndId();
    tripAgencyAndId.setId(tripId);
    tripAgencyAndId.setAgencyId(agency.getId());
    Trip trip = new Trip();
    trip.setId(tripAgencyAndId);

    AgencyAndId serviceAgencyAndId = new AgencyAndId();

    // route
    trip.setRoute(gtfsRoute);

    // direction
    DirectionTypeEnumeration directionType = netexRoute.getDirectionType();
    if (DirectionTypeEnumeration.INBOUND == directionType) {
      trip.setDirectionId(TripProducer.GTFS_DIRECTION_INBOUND);
    } else {
      trip.setDirectionId(TripProducer.GTFS_DIRECTION_OUTBOUND);
    }

    // service
    if (serviceJourney.getDayTypes() != null) {
      Set<DayType> dayTypes = serviceJourney
        .getDayTypes()
        .getDayTypeRef()
        .stream()
        .map(jaxbElement -> jaxbElement.getValue().getRef())
        .map(netexDatasetRepository::getDayTypeById)
        .collect(Collectors.toSet());
      serviceAgencyAndId.setId(
        gtfsServiceRepository.getServiceForDayTypes(dayTypes).getId()
      );
    } else {
      LOGGER.trace(
        "Producing trip based on DatedServiceJourneys for ServiceJourney {}",
        serviceJourney.getId()
      );
      // DatedServiceJourneys for cancelled and replaced trips are filtered out
      Set<OperatingDay> operatingDays = netexDatasetRepository
        .getDatedServiceJourneysByServiceJourneyId(serviceJourney.getId())
        .stream()
        .filter(Predicate.not(serviceAlterationChecker::isReplacedOrCancelled))
        .map(datedServiceJourney ->
          netexDatasetRepository.getOperatingDayById(
            datedServiceJourney.getOperatingDayRef().getRef()
          )
        )
        .collect(Collectors.toSet());
      if (operatingDays.isEmpty()) {
        LOGGER.trace(
          "Filtering ServiceJourney where all DatedServiceJourneys are replaced or cancelled: {}",
          serviceJourney.getId()
        );
        return null;
      }
      serviceAgencyAndId.setId(
        gtfsServiceRepository.getServiceForOperatingDays(operatingDays).getId()
      );
    }
    serviceAgencyAndId.setAgencyId(agency.getId());
    trip.setServiceId(serviceAgencyAndId);

    // destination display = head sign
    trip.setTripHeadsign(
      DestinationDisplayUtil.getHeadSignFromDestinationDisplay(
        initialDestinationDisplay,
        netexDatasetRepository
      )
    );

    // shape
    trip.setShapeId(shapeId);

    return trip;
  }
}

package org.entur.netex.gtfs.export.converters;

import java.util.Set;
import java.util.stream.Collectors;
import org.entur.netex.gtfs.export.producer.DefaultStopProducer;
import org.entur.netex.gtfs.export.producer.StopProducer;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.stop.StopAreaRepository;
import org.rutebanken.netex.model.Quay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopsToGtfsConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    StopsToGtfsConverter.class
  );

  private final StopAreaRepository stopAreaRepository;
  private final GtfsDatasetRepository gtfsDatasetRepository;
  private final StopProducer stopProducer;

  public StopsToGtfsConverter(
    StopAreaRepository stopAreaRepository,
    GtfsDatasetRepository gtfsDatasetRepository
  ) {
    this.stopAreaRepository = stopAreaRepository;
    this.gtfsDatasetRepository = gtfsDatasetRepository;
    this.stopProducer =
      new DefaultStopProducer(stopAreaRepository, gtfsDatasetRepository);
  }

  public void convert() {
    LOGGER.debug("Converting only used stops");

    Set<String> allQuaysId = stopAreaRepository
      .getAllQuays()
      .stream()
      .map(Quay::getId)
      .collect(Collectors.toSet());

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
      .map(stopProducer::produceStopFromStopPlace)
      .distinct()
      .forEach(gtfsDatasetRepository::saveEntity);
  }
}

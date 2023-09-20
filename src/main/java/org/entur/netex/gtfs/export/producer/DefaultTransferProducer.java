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

import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.util.StopUtil;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.ServiceJourneyInterchange;

public class DefaultTransferProducer implements TransferProducer {

  private final NetexDatasetRepository netexDatasetRepository;
  private final GtfsDatasetRepository gtfsDatasetRepository;
  private final boolean generateStaySeatedTransfer;

  public DefaultTransferProducer(
    NetexDatasetRepository netexDatasetRepository,
    GtfsDatasetRepository gtfsDatasetRepository
  ) {
    this(netexDatasetRepository, gtfsDatasetRepository, false);
  }

  public DefaultTransferProducer(
    NetexDatasetRepository netexDatasetRepository,
    GtfsDatasetRepository gtfsDatasetRepository,
    boolean generateStaySeatedTransfer
  ) {
    this.netexDatasetRepository = netexDatasetRepository;
    this.gtfsDatasetRepository = gtfsDatasetRepository;
    this.generateStaySeatedTransfer = generateStaySeatedTransfer;
  }

  @Override
  public Transfer produce(ServiceJourneyInterchange serviceJourneyInterchange) {
    Transfer transfer = new Transfer();

    String fromServiceJourneyId = serviceJourneyInterchange
      .getFromJourneyRef()
      .getRef();
    Trip fromTrip = gtfsDatasetRepository.getTripById(fromServiceJourneyId);
    transfer.setFromTrip(fromTrip);

    String toServiceJourneyId = serviceJourneyInterchange
      .getToJourneyRef()
      .getRef();
    Trip toTrip = gtfsDatasetRepository.getTripById(toServiceJourneyId);
    transfer.setToTrip(toTrip);

    String fromScheduledStopPointId = serviceJourneyInterchange
      .getFromPointRef()
      .getRef();
    Stop fromStop = StopUtil.getGtfsStopFromScheduledStopPointId(
      fromScheduledStopPointId,
      netexDatasetRepository,
      gtfsDatasetRepository
    );
    transfer.setFromStop(fromStop);

    String toScheduledStopPointId = serviceJourneyInterchange
      .getToPointRef()
      .getRef();
    Stop toStop = StopUtil.getGtfsStopFromScheduledStopPointId(
      toScheduledStopPointId,
      netexDatasetRepository,
      gtfsDatasetRepository
    );
    transfer.setToStop(toStop);

    if (
      generateStaySeatedTransfer &&
      Boolean.TRUE.equals(serviceJourneyInterchange.isStaySeated())
    ) {
      transfer.setTransferType(TransferProducer.TRANSFER_STAY_SEATED);
    } else if (Boolean.TRUE.equals(serviceJourneyInterchange.isGuaranteed())) {
      transfer.setTransferType(TransferProducer.TRANSFER_TIMED);
    } else if (serviceJourneyInterchange.getMinimumTransferTime() != null) {
      transfer.setTransferType(TransferProducer.TRANSFER_MINIMAL);
      transfer.setMinTransferTime(
        (int) (serviceJourneyInterchange.getMinimumTransferTime().getSeconds())
      );
    } else if (
      serviceJourneyInterchange.getPriority() != null &&
      serviceJourneyInterchange.getPriority().intValueExact() < 0
    ) {
      transfer.setTransferType(TransferProducer.TRANSFER_NOT_ALLOWED);
    } else {
      transfer.setTransferType(TransferProducer.TRANSFER_RECOMMENDED);
    }

    return transfer;
  }
}

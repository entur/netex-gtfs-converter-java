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

package org.entur.netex.gtfs.export.util;

import java.util.Collection;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.ServiceAlterationEnumeration;
import org.rutebanken.netex.model.ServiceJourney;

/**
 * Check the service alteration status for ServiceJourneys and DatedServiceJourneys.
 */
public class ServiceAlterationChecker {

  private final NetexDatasetRepository netexDatasetRepository;

  public ServiceAlterationChecker(
    NetexDatasetRepository netexDatasetRepository
  ) {
    this.netexDatasetRepository = netexDatasetRepository;
  }

  /**
   * Return true if the service journey has a ServiceAlteration equals to CANCELLATION or REPLACED
   * or if all its dated service journeys are cancelled or replaced.
   *
   * @param serviceJourney the service journey to check
   * @return true if the service journey has a ServiceAlteration equals to CANCELLATION or REPLACED or if all its dated service journeys are cancelled or replaced.
   * @see ServiceAlterationEnumeration
   */
  public boolean isReplacedOrCancelled(ServiceJourney serviceJourney) {
    return (
      ServiceAlterationEnumeration.CANCELLATION ==
      serviceJourney.getServiceAlteration() ||
      ServiceAlterationEnumeration.REPLACED ==
      serviceJourney.getServiceAlteration() ||
      hasAllCancelledOrReplacedDatedServiceJourneys(serviceJourney)
    );
  }

  private boolean hasAllCancelledOrReplacedDatedServiceJourneys(
    ServiceJourney serviceJourney
  ) {
    Collection<DatedServiceJourney> datedServiceJourneys =
      netexDatasetRepository.getDatedServiceJourneysByServiceJourneyId(
        serviceJourney.getId()
      );
    return (
      !datedServiceJourneys.isEmpty() &&
      datedServiceJourneys.stream().allMatch(this::isReplacedOrCancelled)
    );
  }

  /**
   * Return true if the dated service journey has a ServiceAlteration equals to CANCELLATION or REPLACED.
   *
   * @param datedServiceJourney the dated service journey to check
   * @return true if the service journey has a ServiceAlteration equals to CANCELLATION or REPLACED.
   * @see ServiceAlterationEnumeration
   */
  public boolean isReplacedOrCancelled(
    DatedServiceJourney datedServiceJourney
  ) {
    return (
      ServiceAlterationEnumeration.CANCELLATION ==
      datedServiceJourney.getServiceAlteration() ||
      ServiceAlterationEnumeration.REPLACED ==
      datedServiceJourney.getServiceAlteration()
    );
  }
}

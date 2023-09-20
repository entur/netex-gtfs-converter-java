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

package org.entur.netex.gtfs.export.stop;

import java.util.Collection;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;

/**
 * A repository containing NeTEx quays and stop places.
 */
public interface StopAreaRepository {
  /**
   * Return the Quay identified by its id.
   *
   * @param quayId the quay id.
   * @return the quay identified by this id.
   */
  Quay getQuayById(String quayId);

  /**
   * Return the stop place associated to a given quay.
   *
   * @param quayId the id of the quay
   * @return the stop place that contains that quay.
   */
  StopPlace getStopPlaceByQuayId(String quayId);

  /**
   * Return all quays in the repository.
   *
   * @return all quays in the repository.
   */
  Collection<Quay> getAllQuays();
}

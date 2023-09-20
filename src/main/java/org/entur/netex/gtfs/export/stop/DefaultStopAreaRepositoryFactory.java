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

import java.io.InputStream;

/**
 * A stop area repository factory that builds stop area repositories from a NeTEx dataset archive.
 * The dataset can be refreshed at runtime by calling {@link #refreshStopAreaRepository(InputStream)}
 */
public class DefaultStopAreaRepositoryFactory
  implements StopAreaRepositoryFactory {

  private StopAreaRepository stopAreaRepository;

  @Override
  public synchronized StopAreaRepository getStopAreaRepository() {
    if (stopAreaRepository == null) {
      throw new IllegalStateException("The stop area repository is not loaded");
    }
    return stopAreaRepository;
  }

  protected synchronized void setStopAreaRepository(
    StopAreaRepository stopAreaRepository
  ) {
    this.stopAreaRepository = stopAreaRepository;
  }

  /**
   * Refresh the cached stop area.
   *
   * @param stopDataset an input stream on a NeTEX dataset archive.
   */
  public synchronized void refreshStopAreaRepository(InputStream stopDataset) {
    DefaultStopAreaRepository defaultStopAreaRepository =
      new DefaultStopAreaRepository();
    defaultStopAreaRepository.loadStopAreas(stopDataset);
    this.stopAreaRepository = defaultStopAreaRepository;
  }
}

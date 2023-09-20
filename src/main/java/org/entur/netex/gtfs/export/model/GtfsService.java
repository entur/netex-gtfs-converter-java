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

package org.entur.netex.gtfs.export.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A GTFS service representing an operating period and/or a set of explicitly included/excluded dates
 */
public class GtfsService {

  private final String id;

  private ServiceCalendarPeriod serviceCalendarPeriod;
  private final Set<LocalDateTime> includedDates = new HashSet<>();
  private final Set<LocalDateTime> excludedDates = new HashSet<>();

  public GtfsService(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void addExcludedDate(LocalDateTime date) {
    excludedDates.add(date);
  }

  public void addIncludedDate(LocalDateTime date) {
    includedDates.add(date);
  }

  public Set<LocalDateTime> getIncludedDates() {
    return new HashSet<>(includedDates);
  }

  public Set<LocalDateTime> getExcludedDates() {
    return new HashSet<>(excludedDates);
  }

  public ServiceCalendarPeriod getServiceCalendarPeriod() {
    return serviceCalendarPeriod;
  }

  public void setServiceCalendarPeriod(
    ServiceCalendarPeriod serviceCalendarPeriod
  ) {
    this.serviceCalendarPeriod = serviceCalendarPeriod;
  }

  public void removeIncludedDates(Set<LocalDateTime> removedIncludedDates) {
    includedDates.removeAll(removedIncludedDates);
  }

  public void removeAllExcludedDates() {
    excludedDates.clear();
  }
}

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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A GTFS service representing an operating period and/or a set of explicitly included/excluded dates
 */
public class GtfsService {

  private final String id;

  private ServiceCalendarPeriod serviceCalendarPeriod;
  private final Set<LocalDate> includedDates = new HashSet<>();
  private final Set<LocalDate> excludedDates = new HashSet<>();

  public GtfsService(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void addExcludedDate(LocalDate date) {
    excludedDates.add(date);
  }

  public void addIncludedDate(LocalDate date) {
    includedDates.add(date);
  }

  public Set<LocalDate> getIncludedDates() {
    return new HashSet<>(includedDates);
  }

  public Set<LocalDate> getExcludedDates() {
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

  public void removeIncludedDates(Set<LocalDate> removedIncludedDates) {
    includedDates.removeAll(removedIncludedDates);
  }

  public void removeAllExcludedDates() {
    excludedDates.clear();
  }
}

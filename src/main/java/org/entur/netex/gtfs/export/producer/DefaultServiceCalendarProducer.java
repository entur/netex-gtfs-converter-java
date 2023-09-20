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

import java.time.DayOfWeek;
import java.util.Collection;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

public class DefaultServiceCalendarProducer implements ServiceCalendarProducer {

  private final Agency agency;

  public DefaultServiceCalendarProducer(
    GtfsDatasetRepository gtfsDatasetRepository
  ) {
    this.agency = gtfsDatasetRepository.getDefaultAgency();
  }

  @Override
  public ServiceCalendar produce(
    String serviceId,
    ServiceDate startDate,
    ServiceDate endDate,
    Collection<DayOfWeek> daysOfWeeks
  ) {
    ServiceCalendar serviceCalendar = new ServiceCalendar();

    AgencyAndId agencyAndId = new AgencyAndId();
    agencyAndId.setId(serviceId);
    agencyAndId.setAgencyId(agency.getId());
    serviceCalendar.setServiceId(agencyAndId);
    serviceCalendar.setStartDate(startDate);
    serviceCalendar.setEndDate(endDate);

    serviceCalendar.setMonday(
      daysOfWeeks.contains(DayOfWeek.MONDAY)
        ? ServiceCalendarProducer.SERVICE_AVAILABLE
        : ServiceCalendarProducer.SERVICE_UNAVAILABLE
    );
    serviceCalendar.setTuesday(
      daysOfWeeks.contains(DayOfWeek.TUESDAY)
        ? ServiceCalendarProducer.SERVICE_AVAILABLE
        : ServiceCalendarProducer.SERVICE_UNAVAILABLE
    );
    serviceCalendar.setWednesday(
      daysOfWeeks.contains(DayOfWeek.WEDNESDAY)
        ? ServiceCalendarProducer.SERVICE_AVAILABLE
        : ServiceCalendarProducer.SERVICE_UNAVAILABLE
    );
    serviceCalendar.setThursday(
      daysOfWeeks.contains(DayOfWeek.THURSDAY)
        ? ServiceCalendarProducer.SERVICE_AVAILABLE
        : ServiceCalendarProducer.SERVICE_UNAVAILABLE
    );
    serviceCalendar.setFriday(
      daysOfWeeks.contains(DayOfWeek.FRIDAY)
        ? ServiceCalendarProducer.SERVICE_AVAILABLE
        : ServiceCalendarProducer.SERVICE_UNAVAILABLE
    );
    serviceCalendar.setSaturday(
      daysOfWeeks.contains(DayOfWeek.SATURDAY)
        ? ServiceCalendarProducer.SERVICE_AVAILABLE
        : ServiceCalendarProducer.SERVICE_UNAVAILABLE
    );
    serviceCalendar.setSunday(
      daysOfWeeks.contains(DayOfWeek.SUNDAY)
        ? ServiceCalendarProducer.SERVICE_AVAILABLE
        : ServiceCalendarProducer.SERVICE_UNAVAILABLE
    );

    return serviceCalendar;
  }
}

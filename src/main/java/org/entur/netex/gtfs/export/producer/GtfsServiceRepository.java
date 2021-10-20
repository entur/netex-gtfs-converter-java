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

import org.entur.netex.gtfs.export.model.GtfsService;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.OperatingDay;

import java.util.Collection;
import java.util.Set;


/**
 * Repository giving access to the list of GTFS service in the GTFS object model being built.
 */
public interface GtfsServiceRepository {

    /**
     * Return the list of services.
     *
     * @return the list of services.
     */
    Collection<GtfsService> getAllServices();

    /**
     * Create or retrieve the GTFS service corresponding to a set of DayTypes.
     * Multiple calls to this method with the same set of day types return the same object.
     * GTFS services based on day types are used for producing trips from ServiceJourneys (not DatedServiceJourneys).
     *
     * @param dayTypes the set of NeTEx DayTypes.
     * @return the GTFS service for this set of DayTypes.
     */
    GtfsService getServiceForDayTypes(Set<DayType> dayTypes);

    /**
     * Create or retrieve the GTFS service corresponding to a set of OperatingDays.
     * Multiple calls to this method with the same set of operating days return the same object.
     * GTFS services based on operating days are used for producing trips from DatedServiceJourneys.
     *
     * @param operatingDays the set of NeTEx OperatingDays.
     * @return the GTFS service for this set of OperatingDays.
     */
    GtfsService getServiceForOperatingDays(Set<OperatingDay> operatingDays);
}

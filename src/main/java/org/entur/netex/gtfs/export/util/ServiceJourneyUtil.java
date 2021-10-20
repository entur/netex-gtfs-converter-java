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

import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.ServiceAlterationEnumeration;
import org.rutebanken.netex.model.ServiceJourney;

/**
 * Utility class for ServiceJourneys.
 */
public final class ServiceJourneyUtil {
    private ServiceJourneyUtil() {
    }

    /**
     * Return true if the service journey has a ServiceAlteration equals to CANCELLATION or REPLACED.
     * @see ServiceAlterationEnumeration
     * @param serviceJourney the service journey to check
     * @return true if the service journey has a ServiceAlteration equals to CANCELLATION or REPLACED.
     */
    public static boolean isReplacedOrCancelled(ServiceJourney serviceJourney) {
        return ServiceAlterationEnumeration.CANCELLATION == serviceJourney.getServiceAlteration() || ServiceAlterationEnumeration.REPLACED == serviceJourney.getServiceAlteration();
    }

    /**
     * Return true if the dated service journey has a ServiceAlteration equals to CANCELLATION or REPLACED.
     * @see ServiceAlterationEnumeration
     * @param datedServiceJourney the dated service journey to check
     * @return true if the service journey has a ServiceAlteration equals to CANCELLATION or REPLACED.
     */
    public static boolean isReplacedOrCancelled(DatedServiceJourney datedServiceJourney) {
        return ServiceAlterationEnumeration.CANCELLATION == datedServiceJourney.getServiceAlteration() || ServiceAlterationEnumeration.REPLACED == datedServiceJourney.getServiceAlteration();
    }
}

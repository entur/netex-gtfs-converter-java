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

import org.onebusaway.gtfs.model.Agency;
import org.rutebanken.netex.model.Authority;

/**
 * Produce a GTFS Agency
 */
public interface AgencyProducer {

    /**
     * Produce a GTFS agency from a NeTEx authority.
     * @param authority a NeTEx authority
     * @return the GTS agency.
     */
    Agency produce(Authority authority);
}

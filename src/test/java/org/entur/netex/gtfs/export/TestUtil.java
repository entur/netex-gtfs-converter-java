/*
 *
 *  *
 *  *  * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 *  *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  *  * You may not use this work except in compliance with the Licence.
 *  *  * You may obtain a copy of the Licence at:
 *  *  *
 *  *  *   https://joinup.ec.europa.eu/software/page/eupl
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the Licence for the specific language governing permissions and
 *  *  * limitations under the Licence.
 *  *  *
 *  *
 *
 */

package org.entur.netex.gtfs.export;

import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypeRefs_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.ServiceJourney;

public class TestUtil {

    private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();


    public static ServiceJourney createTestServiceJourney(String serviceJourneyId, String dayTypeId) {
        ServiceJourney serviceJourney = new ServiceJourney();
        serviceJourney.setId(serviceJourneyId);
        DayTypeRefs_RelStructure dayTypeStruct = NETEX_FACTORY.createDayTypeRefs_RelStructure();
        serviceJourney.setDayTypes(dayTypeStruct);
        DayTypeRefStructure dayTypeRefStruct = NETEX_FACTORY.createDayTypeRefStructure();
        dayTypeRefStruct.setRef(dayTypeId);
        serviceJourney.getDayTypes().getDayTypeRef().add(NETEX_FACTORY.createDayTypeRef(dayTypeRefStruct));
        return serviceJourney;
    }
}

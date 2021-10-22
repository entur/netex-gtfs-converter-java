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

import org.entur.netex.gtfs.export.repository.DefaultGtfsRepository;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.repository.TestNetexDatasetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypeRefs_RelStructure;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DirectionTypeEnumeration;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.ServiceJourney;

class TripProducerTest {

    private static final String SERVICE_JOURNEY_ID = "ServiceJourney-Id";
    private static final String FRONT_TEXT = "Front Text";

    private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();
    private static final String DAY_TYPE_ID = "ENT:DayType:1";


    @Test
    void testTripProducer() {

        NetexDatasetRepository netexDatasetRepository = new TestNetexDatasetRepository();
        GtfsDatasetRepository gtfsDatasetRepository = new DefaultGtfsRepository();
        GtfsServiceRepository gtfsServiceRepository = new DefaultGtfsServiceRepository("codespace", netexDatasetRepository);

        TripProducer tripProducer = new DefaultTripProducer(netexDatasetRepository, gtfsDatasetRepository, gtfsServiceRepository);

        Route netexRoute = new Route();
        netexRoute.setDirectionType(DirectionTypeEnumeration.INBOUND);
        org.onebusaway.gtfs.model.Route gtfsRoute = new org.onebusaway.gtfs.model.Route();
        AgencyAndId shapeId = new AgencyAndId();
        DestinationDisplay initialDestinationDisplay = new DestinationDisplay();
        MultilingualString frontText = new MultilingualString();
        frontText.setValue(FRONT_TEXT);
        initialDestinationDisplay.setFrontText(frontText);

        ServiceJourney serviceJourney = createTestServiceJourney();

        Trip trip = tripProducer.produce(serviceJourney, netexRoute, gtfsRoute, shapeId, initialDestinationDisplay);

        Assertions.assertNotNull(trip);
        Assertions.assertNotNull(trip.getId());
        Assertions.assertEquals(SERVICE_JOURNEY_ID, trip.getId().getId());
        Assertions.assertEquals(TripProducer.GTFS_DIRECTION_INBOUND, trip.getDirectionId());
        Assertions.assertEquals(FRONT_TEXT, trip.getTripHeadsign());

    }

    private ServiceJourney createTestServiceJourney() {
        ServiceJourney serviceJourney = new ServiceJourney();
        serviceJourney.setId(SERVICE_JOURNEY_ID);
        DayTypeRefs_RelStructure dayTypeStruct = NETEX_FACTORY.createDayTypeRefs_RelStructure();
        serviceJourney.setDayTypes(dayTypeStruct);
        DayTypeRefStructure dayTypeRefStruct = NETEX_FACTORY.createDayTypeRefStructure();
        dayTypeRefStruct.setRef(DAY_TYPE_ID);
        serviceJourney.getDayTypes().getDayTypeRef().add(NETEX_FACTORY.createDayTypeRef(dayTypeRefStruct));
        return serviceJourney;
    }
}

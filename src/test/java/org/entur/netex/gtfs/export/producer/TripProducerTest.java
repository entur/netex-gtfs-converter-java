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

import org.entur.netex.gtfs.export.TestUtil;
import org.entur.netex.gtfs.export.repository.DefaultGtfsRepository;
import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Trip;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DirectionTypeEnumeration;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.ServiceJourney;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TripProducerTest {

    private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();


    private static final String SERVICE_JOURNEY_ID = "ServiceJourney-Id";
    private static final String FRONT_TEXT = "Front Text";

    private static final String TEST_DAY_TYPE_ID = "ENT:DayType:1";
    private static final String CODESPACE = "ENT";


    @Test
    void testTripProducer() {

        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);
        DayType dayType = NETEX_FACTORY.createDayType();
        dayType.setId(TEST_DAY_TYPE_ID);
        when(netexDatasetRepository.getDayTypeById(TEST_DAY_TYPE_ID)).thenReturn(dayType);

        GtfsDatasetRepository gtfsDatasetRepository = new DefaultGtfsRepository();
        GtfsServiceRepository gtfsServiceRepository = new DefaultGtfsServiceRepository(CODESPACE, netexDatasetRepository);

        TripProducer tripProducer = new DefaultTripProducer(netexDatasetRepository, gtfsDatasetRepository, gtfsServiceRepository);

        Route netexRoute = new Route();
        netexRoute.setDirectionType(DirectionTypeEnumeration.INBOUND);
        org.onebusaway.gtfs.model.Route gtfsRoute = new org.onebusaway.gtfs.model.Route();
        AgencyAndId shapeId = new AgencyAndId();
        DestinationDisplay initialDestinationDisplay = new DestinationDisplay();
        MultilingualString frontText = new MultilingualString();
        frontText.setValue(FRONT_TEXT);
        initialDestinationDisplay.setFrontText(frontText);

        ServiceJourney serviceJourney = TestUtil.createTestServiceJourney(SERVICE_JOURNEY_ID, TEST_DAY_TYPE_ID);

        Trip trip = tripProducer.produce(serviceJourney, netexRoute, gtfsRoute, shapeId, initialDestinationDisplay);

        Assertions.assertNotNull(trip);
        Assertions.assertNotNull(trip.getId());
        Assertions.assertEquals(SERVICE_JOURNEY_ID, trip.getId().getId());
        Assertions.assertEquals(TripProducer.GTFS_DIRECTION_INBOUND, trip.getDirectionId());
        Assertions.assertEquals(FRONT_TEXT, trip.getTripHeadsign());

        Assertions.assertEquals(TEST_DAY_TYPE_ID, trip.getServiceId().getId());
    }

}

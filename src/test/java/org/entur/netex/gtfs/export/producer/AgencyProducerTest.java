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

import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.repository.TestNetexDatasetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.Agency;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.ContactStructure;
import org.rutebanken.netex.model.MultilingualString;

class AgencyProducerTest {

    private static final String AUTHORITY_ID = "Authority-ID";
    private static final String AUTHORITY_NAME = "Authority-name";
    private static final String AUTHORITY_URL = "Authority-URL";

    @Test
    void testAgencyProducer() {

        NetexDatasetRepository netexDatasetRepository = new TestNetexDatasetRepository();

        AgencyProducer agencyProducer = new DefaultAgencyProducer(netexDatasetRepository);

        Authority authority = new Authority();
        authority.setId(AUTHORITY_ID);
        MultilingualString name = new MultilingualString();
        name.setValue(AUTHORITY_NAME);
        authority.setName(name);
        ContactStructure contactDetails = new ContactStructure();
        contactDetails.setUrl(AUTHORITY_URL);
        authority.setContactDetails(contactDetails);

        Agency agency = agencyProducer.produce(authority);
        Assertions.assertNotNull(agency);
        Assertions.assertEquals(AUTHORITY_ID, agency.getId());
        Assertions.assertEquals(AUTHORITY_NAME, agency.getName());
        Assertions.assertNotNull(AUTHORITY_URL, agency.getUrl());
    }
}

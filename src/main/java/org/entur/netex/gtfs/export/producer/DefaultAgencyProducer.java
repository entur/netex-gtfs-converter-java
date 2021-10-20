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
import org.onebusaway.gtfs.model.Agency;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.ContactStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAgencyProducer implements AgencyProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAgencyProducer.class);

    private final NetexDatasetRepository netexDatasetRepository;

    public DefaultAgencyProducer(NetexDatasetRepository netexDatasetRepository) {
        this.netexDatasetRepository = netexDatasetRepository;
    }

    @Override
    public Agency produce(Authority authority) {

        String timeZone = netexDatasetRepository.getTimeZone();

        Agency agency = new Agency();
        agency.setId(authority.getId());

        // agency name
        agency.setName(authority.getName().getValue());

        // agency URL and phone
        ContactStructure contactDetails = authority.getContactDetails();
        if (contactDetails != null) {
            String url = contactDetails.getUrl();
            if (url != null && !url.isBlank()) {
                if (isValidGtfsUrl(url)) {
                    agency.setUrl(url);
                } else {
                    LOGGER.warn("Invalid URL format {} for authority {}", url, authority.getId());
                }
            } else {
                LOGGER.warn("Missing URL for authority {}", authority.getId());
            }
            agency.setPhone(contactDetails.getPhone());
        } else {
            LOGGER.warn("Missing Contact details for authority {}", authority.getId());
        }

        // agency timezone
        agency.setTimezone(timeZone);

        return agency;
    }

    /**
     * Return true if the url is a valid GTFS URL, starting with either http:// or https://
     * See https://developers.google.com/transit/gtfs/reference#field_types
     *
     * @param url the agency URL to check
     * @return true if the url is a valid GTFS URL, starting with either http:// or https://
     */
    private static boolean isValidGtfsUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}

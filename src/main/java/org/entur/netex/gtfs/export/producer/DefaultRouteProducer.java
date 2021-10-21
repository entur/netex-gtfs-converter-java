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

import org.entur.netex.gtfs.export.repository.GtfsDatasetRepository;
import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.entur.netex.gtfs.export.util.TransportModeUtil;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.PresentationStructure;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Produce a GTFS Route from a NeTEx Line.
 * Supported GTFS Extension: the route_type field is mapped to GTFS extended route types.
 */
public class DefaultRouteProducer implements RouteProducer {

    private final HexBinaryAdapter hexBinaryAdapter;
    private final NetexDatasetRepository netexDatasetRepository;
    private final GtfsDatasetRepository gtfsDatasetRepository;

    public DefaultRouteProducer(NetexDatasetRepository netexDatasetRepository, GtfsDatasetRepository gtfsDatasetRepository) {
        this.netexDatasetRepository = netexDatasetRepository;
        this.gtfsDatasetRepository = gtfsDatasetRepository;
        this.hexBinaryAdapter = new HexBinaryAdapter();
    }


    @Override
    public Route produce(Line line) {
        String lineId = line.getId();
        Route route = new Route();

        // route agency
        String authorityId = netexDatasetRepository.getAuthorityIdForLine(line);
        Agency agency = gtfsDatasetRepository.getAgencyById(authorityId);
        route.setAgency(agency);

        AgencyAndId agencyAndId = new AgencyAndId();
        agencyAndId.setId(lineId);
        agencyAndId.setAgencyId(agency.getId());
        route.setId(agencyAndId);


        // route short and long names
        // The NeTEx name hierarchy is: public code < short name < name
        // The GTFS name hierarchy is: short name < long name
        // the NeTEx public code becomes the GTFS short name
        route.setShortName(line.getPublicCode());
        // The NeTEx short name becomes the GTFS long name
        if(line.getShortName() != null && line.getShortName().getValue() != null) {
            route.setLongName(line.getShortName().getValue());
        } else {
            // If the NeTEx short name is missing, the NeTEx name becomes the GTFS long name
            route.setLongName(line.getName().getValue());
        }
        // The long name is omitted if it is identical to the short name
        if(route.getLongName() != null && route.getLongName().equals(route.getShortName())) {
            route.setLongName(null);
        }

        // route description
        if (line.getDescription() != null) {
            route.setDesc(line.getDescription().getValue());
        }

        // route URL
        route.setUrl(line.getUrl());

        // route type
       route.setType(TransportModeUtil.getGtfsExtendedRouteType(line));

        // route color
        PresentationStructure presentation = line.getPresentation();
        if (presentation != null) {
            route.setColor(hexBinaryAdapter.marshal(presentation.getColour()));
            route.setTextColor(hexBinaryAdapter.marshal(presentation.getTextColour()));
        }
        return route;
    }
}

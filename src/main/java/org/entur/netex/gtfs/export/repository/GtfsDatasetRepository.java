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

package org.entur.netex.gtfs.export.repository;

import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Location;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;

import java.io.InputStream;

/**
 * Repository that gives read/write access to the GTFS data model being built.
 */
public interface GtfsDatasetRepository {

    /**
     * Return a GTFS agency by id.
     * @param agencyId the agency id
     * @return the GTFS agency
     *  @throws GtfsDatasetRepositoryException if the agency cannot be found in the repository.
     */
    Agency getAgencyById(String agencyId);


    /**
     * Return a GTFS trip by id.
     * @param tripId the trip id
     * @return the GTFS trip
     * @throws GtfsDatasetRepositoryException if the trip cannot be found in the repository.
     */
    Trip getTripById(String tripId);

    /**
     * Return a GTFS stop by id.
     * @param stopId the stop id
     * @return the GTFS stop
     *  @throws GtfsDatasetRepositoryException if the stop cannot be found in the repository.
     */
    Stop getStopById(String stopId);

    /**
     * Add an entity to the in-memory GTFS object model.
     * @param entity the GTFS entity to be saved.
     */
    void saveEntity(Object entity);

    /**
     * Generate a GTFS archive from the GTFS object model and return an input stream pointing to it.
     * @return the GTFS archive
     */
    InputStream writeGtfs();

    Agency getDefaultAgency();

    Location getLocationById(String flexibleStopPlaceId);
}

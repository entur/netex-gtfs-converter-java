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

import java.io.InputStream;
import java.util.Optional;
import org.entur.netex.gtfs.export.serializer.DefaultGtfsSerializer;
import org.entur.netex.gtfs.export.serializer.GtfsSerializer;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.services.GtfsMutableDao;

public class DefaultGtfsRepository implements GtfsDatasetRepository {

  private static final String DEFAULT_AGENCY_ID = "DEFAULT";

  private final GtfsMutableDao gtfsDao;
  private final GtfsSerializer gtfsSerializer;
  private final Agency defaultAgency;

  public DefaultGtfsRepository() {
    this.gtfsDao = new GtfsRelationalDaoImpl();
    this.gtfsSerializer = new DefaultGtfsSerializer();
    this.defaultAgency = createDefaultAgency();
  }

  @Override
  public Agency getAgencyById(String agencyId) {
    Agency agency = gtfsDao.getAgencyForId(agencyId);
    if (agency == null) {
      throw new GtfsDatasetRepositoryException("Agency not found: " + agencyId);
    }
    return agency;
  }

  @Override
  public Trip getTripById(String tripId) {
    return findTripById(tripId)
      .orElseThrow(() ->
        new GtfsDatasetRepositoryException("GTFS Trip not found: " + tripId)
      );
  }

  @Override
  public Optional<Trip> findTripById(String tripId) {
    AgencyAndId agencyAndId = new AgencyAndId(defaultAgency.getId(), tripId);
    return Optional.ofNullable(gtfsDao.getTripForId(agencyAndId));
  }

  @Override
  public Stop getStopById(String stopId) {
    AgencyAndId agencyAndId = new AgencyAndId();
    agencyAndId.setId(stopId);
    agencyAndId.setAgencyId(defaultAgency.getId());
    Stop stop = gtfsDao.getStopForId(agencyAndId);
    if (stop == null) {
      throw new GtfsDatasetRepositoryException("Stop not found: " + stopId);
    }
    return stop;
  }

  @Override
  public void saveEntity(Object entity) {
    gtfsDao.saveEntity(entity);
  }

  @Override
  public InputStream writeGtfs() {
    return gtfsSerializer.writeGtfs(gtfsDao);
  }

  @Override
  public Agency getDefaultAgency() {
    return defaultAgency;
  }

  /**
   * Return a default agency.
   * The OneBusAway API requires an agency linked to GTFS stops, even if it does not appear in the GTFS export.
   *
   * @return a default agency.
   */
  private static Agency createDefaultAgency() {
    Agency defaultAgency = new Agency();
    defaultAgency.setId(DEFAULT_AGENCY_ID);
    return defaultAgency;
  }
}

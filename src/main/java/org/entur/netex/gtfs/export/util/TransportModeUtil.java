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

import static org.entur.netex.gtfs.export.model.GtfsRouteType.*;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.entur.netex.gtfs.export.model.GtfsRouteType;
import org.rutebanken.netex.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for transport modes.
 */
public final class TransportModeUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    TransportModeUtil.class
  );

  /**
   * Represent a pair (TransportMode, TransportSubMode)
   */
  private static class TransportModeAndSubMode
    implements Comparable<TransportModeAndSubMode> {

    private final String transportMode;
    private final String transportSubMode;

    TransportModeAndSubMode(String transportMode, String transportSubMode) {
      this.transportMode = transportMode;
      this.transportSubMode = transportSubMode;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      TransportModeAndSubMode that = (TransportModeAndSubMode) o;
      return (
        transportMode.equals(that.transportMode) &&
        Objects.equals(transportSubMode, that.transportSubMode)
      );
    }

    @Override
    public int hashCode() {
      return Objects.hash(transportMode, transportSubMode);
    }

    @Override
    public int compareTo(TransportModeAndSubMode o) {
      if (this.transportMode.equals(o.transportMode)) {
        return StringUtils.compare(this.transportSubMode, o.transportSubMode);
      }
      return this.transportMode.compareTo(o.transportMode);
    }
  }

  private static final Map<TransportModeAndSubMode, GtfsRouteType> ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE;

  static {
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE = new TreeMap<>();
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.AIR.value(),
        null
      ),
      AIR_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.AIR.value(),
        AirSubmodeEnumeration.DOMESTIC_FLIGHT.value()
      ),
      DOMESTIC_AIR_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.AIR.value(),
        AirSubmodeEnumeration.HELICOPTER_SERVICE.value()
      ),
      HELICOPTER_AIR_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.AIR.value(),
        AirSubmodeEnumeration.INTERNATIONAL_FLIGHT.value()
      ),
      INTERNATIONAL_AIR_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        null
      ),
      BUS_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        BusSubmodeEnumeration.AIRPORT_LINK_BUS.value()
      ),
      BUS_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        BusSubmodeEnumeration.EXPRESS_BUS.value()
      ),
      EXPRESS_BUS_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        BusSubmodeEnumeration.LOCAL_BUS.value()
      ),
      LOCAL_BUS_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        BusSubmodeEnumeration.NIGHT_BUS.value()
      ),
      NIGHT_BUS_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS.value()
      ),
      RAIL_REPLACEMENT_BUS_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        BusSubmodeEnumeration.REGIONAL_BUS.value()
      ),
      REGIONAL_BUS_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        BusSubmodeEnumeration.SCHOOL_BUS.value()
      ),
      SCHOOL_BUS
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        BusSubmodeEnumeration.SHUTTLE_BUS.value()
      ),
      SHUTTLE_BUS
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.BUS.value(),
        BusSubmodeEnumeration.SIGHTSEEING_BUS.value()
      ),
      SIGHTSEEING_BUS
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.COACH.value(),
        null
      ),
      COACH_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.COACH.value(),
        CoachSubmodeEnumeration.INTERNATIONAL_COACH.value()
      ),
      INTERNATIONAL_COACH_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.COACH.value(),
        CoachSubmodeEnumeration.NATIONAL_COACH.value()
      ),
      NATIONAL_COACH_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.COACH.value(),
        CoachSubmodeEnumeration.TOURIST_COACH.value()
      ),
      TOURIST_COACH_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.FERRY.value(),
        null
      ),
      FERRY_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.METRO.value(),
        null
      ),
      METRO_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.RAIL.value(),
        null
      ),
      RAILWAY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.RAIL.value(),
        RailSubmodeEnumeration.INTERNATIONAL.value()
      ),
      LONG_DISTANCE_TRAINS
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.RAIL.value(),
        RailSubmodeEnumeration.LONG_DISTANCE.value()
      ),
      LONG_DISTANCE_TRAINS
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.RAIL.value(),
        RailSubmodeEnumeration.INTERREGIONAL_RAIL.value()
      ),
      INTER_REGIONAL_RAIL_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.RAIL.value(),
        RailSubmodeEnumeration.LOCAL.value()
      ),
      RAILWAY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.RAIL.value(),
        RailSubmodeEnumeration.NIGHT_RAIL.value()
      ),
      SLEEPER_RAIL_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.RAIL.value(),
        RailSubmodeEnumeration.REGIONAL_RAIL.value()
      ),
      REGIONAL_RAIL_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.RAIL.value(),
        RailSubmodeEnumeration.TOURIST_RAILWAY.value()
      ),
      TOURIST_RAILWAY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.RAIL.value(),
        RailSubmodeEnumeration.AIRPORT_LINK_RAIL.value()
      ),
      HIGH_SPEED_RAIL_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.TROLLEY_BUS.value(),
        null
      ),
      TROLLEYBUS_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.TRAM.value(),
        null
      ),
      TRAM_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.TRAM.value(),
        TramSubmodeEnumeration.LOCAL_TRAM.value()
      ),
      LOCAL_TRAM_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.TRAM.value(),
        TramSubmodeEnumeration.CITY_TRAM.value()
      ),
      CITY_TRAM_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.WATER.value(),
        null
      ),
      WATER_TRANSPORT_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.WATER.value(),
        WaterSubmodeEnumeration.HIGH_SPEED_PASSENGER_SERVICE.value()
      ),
      PASSENGER_HIGH_SPEED_FERRY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.WATER.value(),
        WaterSubmodeEnumeration.HIGH_SPEED_VEHICLE_SERVICE.value()
      ),
      CAR_HIGH_SPEED_FERRY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.WATER.value(),
        WaterSubmodeEnumeration.INTERNATIONAL_CAR_FERRY.value()
      ),
      INTERNATIONAL_CAR_FERRY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.WATER.value(),
        WaterSubmodeEnumeration.INTERNATIONAL_PASSENGER_FERRY.value()
      ),
      INTERNATIONAL_PASSENGER_FERRY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.WATER.value(),
        WaterSubmodeEnumeration.LOCAL_CAR_FERRY.value()
      ),
      LOCAL_CAR_FERRY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.WATER.value(),
        WaterSubmodeEnumeration.LOCAL_PASSENGER_FERRY.value()
      ),
      LOCAL_PASSENGER_FERRY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.WATER.value(),
        WaterSubmodeEnumeration.NATIONAL_CAR_FERRY.value()
      ),
      NATIONAL_CAR_FERRY_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.WATER.value(),
        WaterSubmodeEnumeration.SIGHTSEEING_SERVICE.value()
      ),
      SIGHTSEEING_BOAT_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.CABLEWAY.value(),
        null
      ),
      TELECABIN_SERVICE
    );
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.LIFT.value(),
        null
      ),
      TELECABIN_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.FUNICULAR.value(),
        null
      ),
      FUNICULAR_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.TAXI.value(),
        null
      ),
      TAXI_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.TAXI.value(),
        TaxiSubmodeEnumeration.COMMUNAL_TAXI.value()
      ),
      COMMUNAL_TAXI_SERVICE
    );

    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.put(
      new TransportModeAndSubMode(
        AllVehicleModesOfTransportEnumeration.OTHER.value(),
        null
      ),
      MISCELLANEOUS_SERVICE
    );
  }

  private TransportModeUtil() {}

  /**
   * Return the GTFS extended route type code for a given NeTEx Line:
   *
   * @param line a NeTEx line.
   * @return the GTFS extended route type code.
   */
  public static int getGtfsExtendedRouteType(Line line) {
    String transportMode = line.getTransportMode().value();
    String transportSubMode = getSubMode(line.getTransportSubmode());
    return getGtfsExtendedRouteType(transportMode, transportSubMode).getValue();
  }

  /**
   * Return the GTFS extended route type code for a NeTEx netexTransportMode:
   *
   * @param transportMode a NeTEx transport mode.
   * @return the GTFS extended route type code.
   */
  public static int getGtfsExtendedRouteType(
    AllVehicleModesOfTransportEnumeration transportMode
  ) {
    return getGtfsExtendedRouteType(transportMode.value(), null).getValue();
  }

  /**
   * Convert a pair of NeTEx (transport mode, transport sub mode) into a GTFS extended route type.
   *
   * @param transportMode    a NeTEx transport mode.
   * @param transportSubMode a NeTEx transport submode.
   * @return a GTFS extended route type.
   */
  private static GtfsRouteType getGtfsExtendedRouteType(
    String transportMode,
    String transportSubMode
  ) {
    GtfsRouteType routeType = ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.get(
      new TransportModeAndSubMode(transportMode, transportSubMode)
    );
    if (routeType == null) {
      LOGGER.debug(
        "Unknown transport sub mode {}, falling back to route type for parent transport mode {}",
        transportSubMode,
        transportMode
      );
      routeType =
        ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.get(
          new TransportModeAndSubMode(transportMode, null)
        );
    }
    if (routeType == null) {
      LOGGER.debug(
        "Unknown transport mode {}, falling back to route type for miscellaneous services",
        transportMode
      );
      routeType = MISCELLANEOUS_SERVICE;
    }
    return routeType;
  }

  /**
   * Return the sub mode as a String for a given TransportModeStructure, or null if the transport mode is not set.
   *
   * @param subModeStructure a transport sub mode structure.
   * @return the submode as a String for a given TransportModeStructure, or null if the transport mode is not set.
   */
  private static String getSubMode(TransportSubmodeStructure subModeStructure) {
    if (subModeStructure == null) {
      return null;
    }

    if (subModeStructure.getAirSubmode() != null) {
      return subModeStructure.getAirSubmode().value();
    }
    if (subModeStructure.getBusSubmode() != null) {
      return subModeStructure.getBusSubmode().value();
    }
    if (subModeStructure.getCoachSubmode() != null) {
      return subModeStructure.getCoachSubmode().value();
    }
    if (subModeStructure.getFunicularSubmode() != null) {
      return subModeStructure.getFunicularSubmode().value();
    }
    if (subModeStructure.getMetroSubmode() != null) {
      return subModeStructure.getMetroSubmode().value();
    }
    if (subModeStructure.getRailSubmode() != null) {
      return subModeStructure.getRailSubmode().value();
    }
    if (subModeStructure.getTelecabinSubmode() != null) {
      return subModeStructure.getTelecabinSubmode().value();
    }
    if (subModeStructure.getTramSubmode() != null) {
      return subModeStructure.getTramSubmode().value();
    }
    if (subModeStructure.getSnowAndIceSubmode() != null) {
      return subModeStructure.getSnowAndIceSubmode().value();
    }
    if (subModeStructure.getWaterSubmode() != null) {
      return subModeStructure.getWaterSubmode().value();
    }
    return null;
  }

  public static void main(String[] args) {
    printMappingTable();
  }

  private static void printMappingTable() {
    ROUTE_TYPE_FOR_TRANSPORT_MODE_AND_SUB_MODE.forEach((key, value) ->
      System.out.println(
        " | " +
        key.transportMode +
        " | " +
        (key.transportSubMode == null ? "" : key.transportSubMode) +
        " | " +
        value.name() +
        " | " +
        value.getValue() +
        " | "
      )
    );
  }
}

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

package org.entur.netex.gtfs.export.model;

/**
 * Enumeration of GTFS extended route types.
 * See https://developers.google.com/transit/gtfs/reference/extended-route-types
 */
public enum GtfsRouteType {


    // Basic types
    TRAM(0),
    SUBWAY(1),
    RAIL(2),
    BUS(3),
    FERRY(4),
    CABLE(5),
    GONDOLA(6),
    FUNICULAR(7),

    // Extended types : Rail
    RAILWAY_SERVICE(100),
    HIGH_SPEED_RAIL_SERVICE(101),
    LONG_DISTANCE_TRAINS(102),
    INTER_REGIONAL_RAIL_SERVICE(103),
    CAR_TRANSPORT_RAIL_SERVICE(104),
    SLEEPER_RAIL_SERVICE(105),
    REGIONAL_RAIL_SERVICE(106),
    TOURIST_RAILWAY_SERVICE(107),
    RAIL_SHUTTLE_WITHIN_COMPLEX(108),
    SUBURBAN_RAILWAY(109),
    REPLACEMENT_RAIL_SERVICE(110),
    SPECIAL_RAIL_SERVICE(111),
    LORRY_TRANSPORT_RAIL_SERVICE(112),
    ALL_RAIL_SERVICES(113),
    CROSS_COUNTRY_RAIL_SERVICE(114),
    VEHICLE_TRANSPORT_RAIL_SERVICE(115),
    RACK_AND_PINION_RAILWAY(116),
    ADDITIONAL_RAIL_SERVICE(117),

    // Extended types : Coach
    COACH_SERVICE(200),
    INTERNATIONAL_COACH_SERVICE(201),
    NATIONAL_COACH_SERVICE(202),
    SHUTTLE_COACH_SERVICE(203),
    REGIONAL_COACH_SERVICE(204),
    SPECIAL_COACH_SERVICE(205),
    SIGHTSEEING_COACH_SERVICE(206),
    TOURIST_COACH_SERVICE(207),
    COMMUTER_COACH_SERVICE(208),
    ALL_COACH_SERVICES(209),

    // Extended types : Suburban Rail
    SUBURBAN_RAILWAY_SERVICE(300),

    // Extended types : Urban Rail
    URBAN_RAILWAY_SERVICE(400),
    METRO_SERVICE(401),
    UNDERGROUND_SERVICE(402),
    URBAN_RAILWAY_SERVICE_2(403),
    ALL_URBAN_RAILWAY_SERVICES(404),
    MONORAIL(405),

    // Extended types : Metro
    METRO_SERVICE_2(500),

    // Extended types : Underground
    UNDERGROUND_SERVICE_2(600),

    // Extended types : Bus
    BUS_SERVICE(700),
    REGIONAL_BUS_SERVICE(701),
    EXPRESS_BUS_SERVICE(702),
    STOPPING_BUS_SERVICE(703),
    LOCAL_BUS_SERVICE(704),
    NIGHT_BUS_SERVICE(705),
    POST_BUS_SERVICE(706),
    SPECIAL_NEEDS_BUS(707),
    MOBILITY_BUS_SERVICE(708),
    MOBILITY_BUS_FOR_REGISTERED_DISABLED(709),
    SIGHTSEEING_BUS(710),
    SHUTTLE_BUS(711),
    SCHOOL_BUS(712),
    SCHOOL_AND_PUBLIC_SERVICE_BUS(713),
    RAIL_REPLACEMENT_BUS_SERVICE(714),
    DEMAND_AND_RESPONSE_BUS_SERVICE(715),
    ALL_BUS_SERVICES(716),

    // Extended types : Trolleybus
    TROLLEYBUS_SERVICE(800),

    // Extended types : Tram
    TRAM_SERVICE(900),
    CITY_TRAM_SERVICE(901),
    LOCAL_TRAM_SERVICE(902),
    REGIONAL_TRAM_SERVICE(903),
    SIGHTSEEING_TRAM_SERVICE(904),
    SHUTTLE_TRAM_SERVICE(905),
    ALL_TRAM_SERVICES(906),

    // Extended types : Water
    WATER_TRANSPORT_SERVICE(1000),
    INTERNATIONAL_CAR_FERRY_SERVICE(1001),
    NATIONAL_CAR_FERRY_SERVICE(1002),
    REGIONAL_CAR_FERRY_SERVICE(1003),
    LOCAL_CAR_FERRY_SERVICE(1004),
    INTERNATIONAL_PASSENGER_FERRY_SERVICE(1005),
    NATIONAL_PASSENGER_FERRY_SERVICE(1006),
    REGIONAL_PASSENGER_FERRY_SERVICE(1007),
    LOCAL_PASSENGER_FERRY_SERVICE(1008),
    POST_BOAT_SERVICE(1009),
    TRAIN_FERRY_SERVICE(1010),
    ROAD_LINK_FERRY_SERVICE(1011),
    AIRPORT_LINK_FERRY_SERVICE(1012),
    CAR_HIGH_SPEED_FERRY_SERVICE(1013),
    PASSENGER_HIGH_SPEED_FERRY_SERVICE(1014),
    SIGHTSEEING_BOAT_SERVICE(1015),
    SCHOOL_BOAT(1016),
    CABLE_DRAWN_BOAT_SERVICE(1017),
    RIVER_BUS_SERVICE(1018),
    SCHEDULED_FERRY_SERVICE(1019),
    SHUTTLE_FERRY_SERVICE(1020),
    ALL_WATER_TRANSPORT_SERVICES(1021),

    // Extended types : Air
    AIR_SERVICE(1100),
    INTERNATIONAL_AIR_SERVICE(1101),
    DOMESTIC_AIR_SERVICE(1102),
    INTERCONTINENTAL_AIR_SERVICE(1103),
    DOMESTIC_SCHEDULED_AIR_SERVICE(1104),
    SHUTTLE_AIR_SERVICE(1105),
    INTERCONTINENTAL_CHARTER_AIR_SERVICE(1106),
    INTERNATIONAL_CHARTER_AIR_SERVICE(1107),
    ROUND_TRIP_CHARTER_AIR_SERVICE(1108),
    SIGHTSEEING_AIR_SERVICE(1109),
    HELICOPTER_AIR_SERVICE(1110),
    DOMESTIC_CHARTER_AIR_SERVICE(1111),
    SCHENGEN_AREA_AIR_SERVICE(1112),
    AIRSHIP_SERVICE(1113),
    ALL_AIR_SERVICES(1114),

    // Extended types : Ferry
    FERRY_SERVICE(1200),

    // Extended types : Telecabin
    TELECABIN_SERVICE(1300),
    TELECABIN_SERVICE_2(1301),
    CABLE_CAR_SERVICE(1302),
    ELEVATOR_SERVICE(1303),
    CHAIR_LIFT_SERVICE(1304),
    DRAG_LIFT_SERVICE(1305),
    SMALL_TELECABIN_SERVICE(1306),
    ALL_TELECABIN_SERVICES(1307),

    // Extended types : Funicular
    FUNICULAR_SERVICE(1400),
    FUNICULAR_SERVICE_2(1401),
    ALL_FUNICULAR_SERVICE(1402),

    // Extended types : Taxi
    TAXI_SERVICE(1500),
    COMMUNAL_TAXI_SERVICE(1501),
    WATER_TAXI_SERVICE(1502),
    RAIL_TAXI_SERVICE(1503),
    BIKE_TAXI_SERVICE(1504),
    LICENSED_TAXI_SERVICE(1505),
    PRIVATE_HIRE_SERVICE_VEHICLE(1506),
    ALL_TAXI_SERVICES(1507),

    // Extended types : Self-drive
    SELF_DRIVE(1600),
    HIRE_CAR(1601),
    HIRE_VAN(1602),
    HIRE_MOTORBIKE(1603),
    HIRE_CYCLE(1604),

    /// Extended types : Miscellaneous
    MISCELLANEOUS_SERVICE(1700),
    CABLE_CAR(1701),
    HORSE_DRAWN_CARRIAGE(1702);

    private final int value;

    GtfsRouteType(final int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public int getValue() {
        return value;
    }
}

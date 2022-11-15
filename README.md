
# NeTEx-to-GTFS Converter Java

[![CircleCI](https://circleci.com/gh/entur/netex-gtfs-converter-java/tree/main.svg?style=svg)](https://circleci.com/gh/entur/netex-gtfs-converter-java/tree/main)

Converts NeTEX datasets into GTFS datasets.
The input NeTEx datasets are required to follow the [Nordic NeTEx Profile](https://enturas.atlassian.net/wiki/spaces/PUBLIC/pages/728891481/Nordic+NeTEx+Profile).

# Input data
The converter requires:
- The NeTEx codespace of the timetable data provider.
- A NeTEx dataset containing the timetable data.
- A NeTEx dataset containing the full definition of the StopPlaces and Quays referred from the timetable data. 

# Output
The converter produces a GTFS zip archive containing timetable data.

# Data format prerequisites
The library supports out-of-the-box NeTEx datasets that follow the Nordic NeTEx Profile and assumes that the dataset structure and completeness are compatible with the Nordic NeTEx Profile (example: required name on a Line, required DestinationDisplay on the first stop of a JourneyPattern, ...).
Supporting other NeTEx profiles is possible by overriding the default conversion process (see Extension points below).

# Usage

        InputStream stopsAndQuaysDataset = // input stream pointing to a zip archive containing the NeTEX stops and quays definitions.
        DefaultStopAreaRepository defaultStopAreaRepository = new DefaultStopAreaRepository();
        defaultStopAreaRepository.loadStopAreas(stopsAndQuaysDataset);

        InputStream netexTimetableDataset = // input stream pointing to a zip archive containing the NeTEX timetable data.
        String codespace = // NeTEX codespace for the timetable data provider.

        GtfsExporter gtfsExport = new DefaultGtfsExporter(codespace, defaultStopAreaRepository);

        // the returned Inputstream points to a GTFS zip archive
        InputStream exportedGtfs = gtfsExport.convertNetexToGtfs(netexTimetableDataset);


# Extension points
GTFS entities are created by Producers interfaces (AgencyProducer, TripProducer, ...)
The library contains default implementations for these interfaces. They can be overridden in order to customize the conversion process.
The NetexDatasetLoader interface specifies the way NeTEx data is loaded into memory. The default implementation can also be overridden.
Example:

    public class EnturGtfsExporter extends DefaultGtfsExporter {
        public EnturGtfsExporter(String codespace, StopAreaRepository stopAreaRepository) {
            super(codespace, stopAreaRepository);
            setNetexDatasetLoader(new EnturNetexDatasetLoader());
            setAgencyProducer(new EnturAgencyProducer(getNetexDatasetRepository(), codespace));
            setFeedInfoProducer(new EnturFeedInfoProducer());
    }

    GtfsExporter gtfsExport = new EnturGtfsExporter(codespace, defaultStopAreaRepository);
}

# Route Type Mapping

The table below contains the mapping between NeTEx transport modes/submodes and GTFS route types.  
This mapping is a combination of:
- [The official GTFS extended route types](https://developers.google.com/transit/gtfs/reference/extended-route-types)
- [A proposal to use TPEG-PTI types](https://groups.google.com/g/gtfs-changes/c/keT5rTPS7Y0/m/71uMz2l6ke0J)

Please note that NeTEx allows for overriding transport modes/submodes at the ServiceJourney level, while GTFS allows only one route type per route.  
In case a NeTEx line makes use of multiple transport modes/submodes, only the transport mode/submode defined at the Line level is used in the conversion process.  

| NeTEx mode |        NeTEx submode        |                  GTFS route type name | GTFS route type code |
|------------|:---------------------------:|--------------------------------------:|----------------------|
| air        |                             |                           AIR_SERVICE | 1100                 | 
| air        |       domesticFlight        |                  DOMESTIC_AIR_SERVICE | 1102                 | 
| air        |      helicopterService      |                HELICOPTER_AIR_SERVICE | 1110                 | 
| air        |     internationalFlight     |             INTERNATIONAL_AIR_SERVICE | 1101                 | 
| bus        |                             |                           BUS_SERVICE | 700                  | 
| bus        |       airportLinkBus        |                           BUS_SERVICE | 700                  | 
| bus        |         expressBus          |                   EXPRESS_BUS_SERVICE | 702                  | 
| bus        |          localBus           |                     LOCAL_BUS_SERVICE | 704                  | 
| bus        |          nightBus           |                     NIGHT_BUS_SERVICE | 705                  | 
| bus        |     railReplacementBus      |          RAIL_REPLACEMENT_BUS_SERVICE | 714                  | 
| bus        |         regionalBus         |                  REGIONAL_BUS_SERVICE | 701                  | 
| bus        |          schoolBus          |                            SCHOOL_BUS | 712                  | 
| bus        |         shuttleBus          |                           SHUTTLE_BUS | 711                  | 
| bus        |       sightseeingBus        |                       SIGHTSEEING_BUS | 710                  | 
| cableway   |                             |                     TELECABIN_SERVICE | 1300                 | 
| coach      |                             |                         COACH_SERVICE | 200                  | 
| coach      |     internationalCoach      |           INTERNATIONAL_COACH_SERVICE | 201                  | 
| coach      |        nationalCoach        |                NATIONAL_COACH_SERVICE | 202                  | 
| coach      |        touristCoach         |                 TOURIST_COACH_SERVICE | 207                  | 
| ferry      |                             |                         FERRY_SERVICE | 1200                 | 
| funicular  |                             |                     FUNICULAR_SERVICE | 1400                 | 
| lift       |                             |                     TELECABIN_SERVICE | 1300                 | 
| metro      |                             |                         METRO_SERVICE | 401                  | 
| other      |                             |                 MISCELLANEOUS_SERVICE | 1700                 | 
| rail       |                             |                       RAILWAY_SERVICE | 100                  | 
| rail       |       airportLinkRail       |               HIGH_SPEED_RAIL_SERVICE | 101                  | 
| rail       |        international        |                  LONG_DISTANCE_TRAINS | 102                  | 
| rail       |      interregionalRail      |           INTER_REGIONAL_RAIL_SERVICE | 103                  | 
| rail       |            local            |                       RAILWAY_SERVICE | 100                  | 
| rail       |        longDistance         |                  LONG_DISTANCE_TRAINS | 102                  | 
| rail       |          nightRail          |                  SLEEPER_RAIL_SERVICE | 105                  | 
| rail       |        regionalRail         |                 REGIONAL_RAIL_SERVICE | 106                  | 
| rail       |       touristRailway        |               TOURIST_RAILWAY_SERVICE | 107                  | 
| taxi       |                             |                          TAXI_SERVICE | 1500                 | 
| tram       |                             |                          TRAM_SERVICE | 900                  | 
| tram       |          cityTram           |                     CITY_TRAM_SERVICE | 901                  | 
| tram       |          localTram          |                    LOCAL_TRAM_SERVICE | 902                  | 
| trolleyBus |                             |                    TROLLEYBUS_SERVICE | 800                  | 
| water      |                             |               WATER_TRANSPORT_SERVICE | 1000                 | 
| water      |  highSpeedPassengerService  |    PASSENGER_HIGH_SPEED_FERRY_SERVICE | 1014                 | 
| water      |   highSpeedVehicleService   |          CAR_HIGH_SPEED_FERRY_SERVICE | 1013                 | 
| water      |    internationalCarFerry    |       INTERNATIONAL_CAR_FERRY_SERVICE | 1001                 | 
| water      | internationalPassengerFerry | INTERNATIONAL_PASSENGER_FERRY_SERVICE | 1005                 | 
| water      |        localCarFerry        |               LOCAL_CAR_FERRY_SERVICE | 1004                 | 
| water      |     localPassengerFerry     |         LOCAL_PASSENGER_FERRY_SERVICE | 1008                 | 
| water      |      nationalCarFerry       |            NATIONAL_CAR_FERRY_SERVICE | 1002                 | 
| water      |     sightseeingService      |              SIGHTSEEING_BOAT_SERVICE | 1015                 | 












# NeTEx-to-GTFS Converter Java

[![CircleCI](https://circleci.com/gh/entur/netex-gtfs-converter-java/tree/master.svg?style=svg)](https://circleci.com/gh/entur/netex-gtfs-converter-java/tree/master)

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



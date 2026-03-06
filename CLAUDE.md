# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java library that converts NeTEx datasets (Nordic NeTEx Profile) into GTFS datasets. Published to Maven Central by Entur. Single-module Maven project, Java 17.

## Build Commands

```bash
# Build with tests (skipping prettier for speed)
mvn package -Dprettier.skip=true

# Run all tests (skipping prettier)
mvn test -Dprettier.skip=true

# Run a single test class
mvn test -Dprettier.skip=true -Dtest=GtfsExportTest

# Run a single test method
mvn test -Dprettier.skip=true -Dtest=GtfsExportTest#testExportSimpleLine

# Format code with prettier (run before committing)
mvn prettier:write

# Check formatting only (as CI does)
mvn validate -PprettierCheck -Dprettier.nodePath=node -Dprettier.npmPath=npm
```

Prettier runs automatically during the `validate` phase with the `write` goal. Skip it during development with `-Dprettier.skip=true` and run it once before finalizing.

## Architecture

### Entry Point
- `GtfsExporter` — interface with two methods: `convertTimetablesToGtfs()` and `convertStopsToGtfs()`
- `DefaultGtfsExporter` — orchestrates the full conversion pipeline: load NeTEx → convert agencies, stops, routes, services, transfers → serialize GTFS

### Package Structure (`org.entur.netex.gtfs.export`)

| Package | Purpose |
|---|---|
| `producer/` | **Producers** — one per GTFS entity type (Agency, Route, Trip, StopTime, Shape, ServiceCalendar, Transfer, Stop, FeedInfo). Each has an interface and a `Default*` implementation. This is the main extension point. |
| `repository/` | `NetexDatasetRepository` holds parsed NeTEx data in memory. `GtfsDatasetRepository` accumulates GTFS entities and serializes to ZIP. |
| `loader/` | `NetexDatasetLoader` — loads NeTEx ZIP into `NetexDatasetRepository`. Uses `netex-parser-java`. |
| `stop/` | `StopAreaRepository` — loads and provides access to NeTEx stop places and quays from a separate stop dataset. |
| `model/` | Internal models: `GtfsService`, `GtfsShape`, `GtfsRouteType`, `ServiceCalendarPeriod` |
| `serializer/` | GTFS serialization using the OneBusAway GTFS library |
| `util/` | Helpers for transport modes, destination displays, geometry, stop lookups |
| `exception/` | Domain-specific exceptions |

### Extension Pattern
Consumers extend `DefaultGtfsExporter` and override specific producers via setter methods (`setAgencyProducer()`, `setRouteProducer()`, etc.). The `NetexDatasetLoader` can also be replaced.

### Key Dependencies
- **netex-java-model** / **netex-parser-java** — NeTEx XML parsing (Entur libraries)
- **onebusaway-gtfs** — GTFS data model and serialization
- **JTS / GeoTools** — geometry operations for shapes

## Testing

Tests use JUnit 5 + Mockito. The integration test `GtfsExportTest` runs a full NeTEx-to-GTFS conversion using test datasets in `src/test/resources/`. Unit tests for individual producers are in `src/test/java/.../producer/`.

## CI

GitHub Actions workflow (`.github/workflows/push.yml`): builds with `mvn package -PprettierCheck`, runs Sonar analysis, and publishes releases to Maven Central on main branch pushes.

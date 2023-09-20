package org.entur.netex.gtfs.export.stop;

/**
 * Retrieve a NeTEx entity from an external source.
 * This can be used as a fallback method when the stop dataset loaded in memory is outdated and misses some quays/stops.
 */
public interface NetexEntityFetcher<R, S> {
  R tryFetch(S s);
}

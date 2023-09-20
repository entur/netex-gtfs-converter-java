package org.entur.netex.gtfs.export.stop;

public interface NetexEntityFetcher<R, S> {
  R tryFetch(S s);
}

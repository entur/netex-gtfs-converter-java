package org.entur.netex.gtfs.export.util;

import java.time.LocalTime;

/**
 * Utility class for converting NeTEx values to GTFS-compatible values.
 */
public final class GtfsUtil {

    private GtfsUtil() {
    }

    /**
     * Return the number of seconds since midnight for this local time.
     *
     * @param netexTime a NeTEx time.
     * @return the GTFS time represented by the number of seconds since midnight for this local time.
     */
    public static int toGtfsTime(LocalTime netexTime) {
        return netexTime.toSecondOfDay();
    }

    /**
     * Return the number of seconds since midnight for this local time plus the number of seconds (positive or negative) corresponding to the day offset.
     * @param netexTime a NeTEx time.
     * @param dayOffset a NeTEx day offset.
     * @return the GTFS time represented by the number of seconds since midnight for this local time plus the number of seconds (positive or negative) corresponding to the day offset.
     */
    public static int toGtfsTimeWithDayOffset(LocalTime netexTime, int dayOffset) {
        return toGtfsTime(netexTime) + dayOffset * 60 * 60 * 24;
    }

}

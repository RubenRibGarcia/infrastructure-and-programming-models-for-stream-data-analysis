package org.isel.thesis.impads.metrics;

public final class ObservableUtils {

    private ObservableUtils() { }

    public static long eventTimeLatencyInMillis(long now, long eventTimestamp) {
        return now - eventTimestamp;
    }

    public static long processingTimeLatencyInMillis(long now, long ingestionTimestamp) {
        return now - ingestionTimestamp;
    }
}

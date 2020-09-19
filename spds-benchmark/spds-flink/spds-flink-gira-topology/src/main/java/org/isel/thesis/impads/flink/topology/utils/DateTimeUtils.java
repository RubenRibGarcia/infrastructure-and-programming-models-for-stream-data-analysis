package org.isel.thesis.impads.flink.topology.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class DateTimeUtils {

    private DateTimeUtils() { }

    public static long toEpochMilli(int year
            , int month
            , int dayOfMonth
            , int hour
            , int minute) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();
    }
}

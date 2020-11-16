package org.isel.thesis.impads.kafka.stream.topology.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class IpmaUtils {

    public static final String instantToHashField(Instant instant) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Lisbon"));
        StringBuilder sb = new StringBuilder();
        sb.append(localDateTime.getMonthValue());
        sb.append(localDateTime.getDayOfMonth());
        sb.append(localDateTime.getHour());

        return sb.toString();
    }

}

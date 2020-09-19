package org.isel.thesis.impads.giragen.data.api;

import java.time.Instant;

public interface EventTimestampGenerator {

    Instant getNextInstant();
}

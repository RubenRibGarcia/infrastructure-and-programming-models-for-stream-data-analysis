package org.isel.thesis.impads.giragen.data.func;

import org.isel.thesis.impads.giragen.data.api.EventTimestampGenerator;

import java.time.Instant;

public class CurrentEventTimestampSynthesizer implements EventTimestampGenerator {

    CurrentEventTimestampSynthesizer() { }

    @Override
    public Instant getNextInstant() {
        return Instant.now();
    }
}

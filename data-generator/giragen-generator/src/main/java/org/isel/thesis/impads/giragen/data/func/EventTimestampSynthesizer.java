package org.isel.thesis.impads.giragen.data.func;

import org.isel.thesis.impads.giragen.data.api.EventTimestampGenerator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class EventTimestampSynthesizer implements EventTimestampGenerator {

    private Instant currentInstant = Instant.ofEpochMilli(1546300800000L);
    private static final long instantMilliIncrement = 1L;

    EventTimestampSynthesizer() { }

    public Instant getNextInstant() {
        synchronized (this) {
            this.setCurrentInstant(this.currentInstant.plus(instantMilliIncrement, ChronoUnit.MILLIS));
            return this.currentInstant;
        }
    }

    private void setCurrentInstant(Instant currentInstant) {
        this.currentInstant = currentInstant;
    }
}

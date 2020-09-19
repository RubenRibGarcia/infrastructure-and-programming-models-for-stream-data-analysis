package org.isel.thesis.impads.giragen.data.func;

import org.isel.thesis.impads.giragen.data.api.EventTimestampGenerator;

public final class FactoryEventTimestampGenerator {

    public static EventTimestampGenerator newCurrentEventTimestampSynthesizer() {
        return new CurrentEventTimestampSynthesizer();
    }

    public static EventTimestampGenerator newEventTimestampSynthesizer() {
        return new EventTimestampSynthesizer();
    }
}

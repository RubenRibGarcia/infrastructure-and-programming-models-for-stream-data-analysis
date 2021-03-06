package org.isel.thesis.impads.flink.fasterxml.jackson.deserializers;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;

public class InstanteDeserializer extends StdDeserializer<Instant> {

    private static final String FIELD_EPOCH_SECOND = "epochSecond";
    private static final String FIELD_NANO = "nano";

    public InstanteDeserializer() {
        this(null);
    }

    public InstanteDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        long epochSeconds = node.get(FIELD_EPOCH_SECOND).longValue();
        long nano = node.get(FIELD_NANO).longValue();

        return Instant.ofEpochSecond(epochSeconds, nano);
    }
}

package org.isel.thesis.impads.kafka.stream.topology.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonDeserializer {

    public static <T> T deserialize(ObjectMapper mapper, byte[] bytes, Class<T> klass) {
        try {
            return mapper.readValue(bytes, klass);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

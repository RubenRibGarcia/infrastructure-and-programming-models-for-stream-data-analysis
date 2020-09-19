package org.isel.thesis.impads.kafka.stream.serdes.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class JsonDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper mapper;
    private final Class<T> klass;

    private JsonDeserializer(ObjectMapper mapper
            , Class<T> klass) {
        this.mapper = mapper;
        this.klass = klass;
    }

    public static <T> JsonDeserializer<T> newJsonDeserializer(ObjectMapper mapper, Class<T> klass) {
        return new JsonDeserializer<>(mapper, klass);
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        try {
            return mapper.readValue(bytes, klass);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

package org.isel.thesis.impads.kafka.stream.serdes.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerializer<T> implements Serializer<T> {

    private final ObjectMapper mapper;

    private JsonSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static <T> JsonSerializer<T> newJsonSerializer(ObjectMapper mapper) {
        return new JsonSerializer<>(mapper);
    }

    @Override
    public byte[] serialize(String topic, T t) {
        try {
            return this.mapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

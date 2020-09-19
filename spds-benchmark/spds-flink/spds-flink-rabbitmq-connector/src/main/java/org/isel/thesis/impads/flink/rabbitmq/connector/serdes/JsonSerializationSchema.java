package org.isel.thesis.impads.flink.rabbitmq.connector.serdes;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializationSchema<T> implements SerializationSchema<T> {

    private final ObjectMapper mapper;

    private JsonSerializationSchema(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static <T> JsonSerializationSchema<T> newJsonSerializationSchema(ObjectMapper mapper) {
        return new JsonSerializationSchema<>(mapper);
    }

    @Override
    public byte[] serialize(T t) {
        try {
            return mapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

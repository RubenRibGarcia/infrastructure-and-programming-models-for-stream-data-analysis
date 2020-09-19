package org.isel.thesis.impads.flink.rabbitmq.connector.serdes;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonDeserializationSchema<T> extends AbstractDeserializationSchema<T> {

    private final ObjectMapper mapper;

    private JsonDeserializationSchema(ObjectMapper mapper, TypeInformation<T> type) {
        super(type);
        this.mapper = mapper;
    }

    private JsonDeserializationSchema(ObjectMapper mapper, TypeHint<T> hint) {
        super(hint);
        this.mapper = mapper;
    }

    public static <T> JsonDeserializationSchema<T> newJsonDeserializationSchema(ObjectMapper mapper, TypeInformation<T> type) {
        return new JsonDeserializationSchema<T>(mapper, type);
    }

    @Override
    public T deserialize(byte[] bytes) throws IOException {
        return mapper.readValue(bytes, getProducedType().getTypeClass());
    }
}

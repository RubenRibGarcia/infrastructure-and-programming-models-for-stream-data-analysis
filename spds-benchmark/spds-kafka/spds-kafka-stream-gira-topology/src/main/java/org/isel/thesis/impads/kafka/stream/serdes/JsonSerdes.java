package org.isel.thesis.impads.kafka.stream.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.isel.thesis.impads.kafka.stream.serdes.deserializer.JsonDeserializer;
import org.isel.thesis.impads.kafka.stream.serdes.serializer.JsonSerializer;

public class JsonSerdes<T> implements Serde<T> {

    private final ObjectMapper mapper;
    private final Class<T> klass;

    private JsonSerdes(ObjectMapper mapper, Class<T> klass) {
        this.mapper = mapper;
        this.klass = klass;
    }

    public static <T> JsonSerdes<T> newJsonSerders(ObjectMapper mapper, Class<T> klass) {
        return new JsonSerdes<>(mapper, klass);
    }

    @Override
    public Serializer<T> serializer() {
        return JsonSerializer.newJsonSerializer(mapper);
    }

    @Override
    public Deserializer<T> deserializer() {
        return JsonDeserializer.newJsonDeserializer(mapper, klass);
    }
}

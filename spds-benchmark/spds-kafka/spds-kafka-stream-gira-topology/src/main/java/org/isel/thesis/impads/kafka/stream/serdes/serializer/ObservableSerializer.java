package org.isel.thesis.impads.kafka.stream.serdes.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.isel.thesis.impads.metrics.api.Observable;

public class ObservableSerializer<T> implements Serializer<Observable<T>> {

    private final ObjectMapper mapper;

    private ObservableSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static <T> ObservableSerializer<T> newJsonSerializer(ObjectMapper mapper) {
        return new ObservableSerializer<>(mapper);
    }


    @Override
    public byte[] serialize(String s, Observable<T> t) {
        try {
            return this.mapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

package org.isel.thesis.impads.kafka.stream.serdes.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.isel.thesis.impads.metrics.api.Observable;

import java.io.IOException;

public class ObservableDeserializer<T> implements Deserializer<Observable<T>> {

    private final ObjectMapper mapper;
    private final Class<T> klass;

    private ObservableDeserializer(ObjectMapper mapper
            , Class<T> klass) {
        this.mapper = mapper;
        this.klass = klass;
    }

    public static <T> ObservableDeserializer<T> newMeasureWrapperDeserializer(ObjectMapper mapper, Class<T> klass) {
        return new ObservableDeserializer<>(mapper, klass);
    }

    @Override
    public Observable<T> deserialize(String topic, byte[] bytes) {
        try {
            return (Observable<T>) mapper.readValue(bytes, klass);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

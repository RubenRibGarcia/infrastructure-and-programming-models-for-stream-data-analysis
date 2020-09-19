package org.isel.thesis.impads.kafka.stream.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.isel.thesis.impads.kafka.stream.serdes.deserializer.ObservableDeserializer;
import org.isel.thesis.impads.kafka.stream.serdes.serializer.ObservableSerializer;
import org.isel.thesis.impads.metrics.api.Observable;

public class ObservableSerdes<T> implements Serde<Observable<T>> {

    private final ObjectMapper mapper;
    private final Class<T> klass;

    private ObservableSerdes(ObjectMapper mapper, Class<T> klass) {
        this.mapper = mapper;
        this.klass = klass;
    }

    public static <T> ObservableSerdes<T> newMeasureWrapperSerdes(ObjectMapper mapper, Class<T> klass) {
        return new ObservableSerdes<>(mapper, klass);
    }

    @Override
    public Serializer<Observable<T>> serializer() {
        return ObservableSerializer.newJsonSerializer(mapper);
    }

    @Override
    public Deserializer<Observable<T>> deserializer() {
        return ObservableDeserializer.newMeasureWrapperDeserializer(mapper, klass);
    }
}

package org.isel.thesis.impads.kafka.stream.serdes.deserializer;

import org.apache.kafka.common.serialization.Deserializer;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.metrics.api.Observable;

public class ObservableDeserializer<T> implements Deserializer<Observable<T>> {

    private final Deserializer<T> deserializer;

    private ObservableDeserializer(Deserializer<T> deserializer) {
        this.deserializer = deserializer;
    }

    public static <T> ObservableDeserializer<T> newObservableDeserializer(Deserializer<T> deserializer) {
        return new ObservableDeserializer<>(deserializer);
    }

    @Override
    public Observable<T> deserialize(String topic, byte[] bytes) {
        return ObservableImpl.of(deserializer.deserialize(topic, bytes), 0l, 0l);
    }
}

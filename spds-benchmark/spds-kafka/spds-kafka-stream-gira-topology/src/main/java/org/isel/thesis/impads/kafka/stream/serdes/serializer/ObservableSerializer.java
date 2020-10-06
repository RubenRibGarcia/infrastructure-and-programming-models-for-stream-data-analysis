package org.isel.thesis.impads.kafka.stream.serdes.serializer;

import org.apache.kafka.common.serialization.Serializer;
import org.isel.thesis.impads.metrics.api.Observable;

public class ObservableSerializer<T> implements Serializer<Observable<T>> {

    private final Serializer<T> serializer;

    private ObservableSerializer(Serializer<T> serializer) {
        this.serializer = serializer;
    }

    public static <T> ObservableSerializer<T> newObservableSerializer(Serializer<T> serializer) {
        return new ObservableSerializer<>(serializer);
    }


    @Override
    public byte[] serialize(String s, Observable<T> t) {
        return serializer.serialize(s, t.getData());
    }
}

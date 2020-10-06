package org.isel.thesis.impads.kafka.stream.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.isel.thesis.impads.kafka.stream.serdes.deserializer.ObservableDeserializer;
import org.isel.thesis.impads.kafka.stream.serdes.serializer.ObservableSerializer;
import org.isel.thesis.impads.metrics.api.Observable;

import java.util.function.Function;
import java.util.function.Supplier;

public class ObservableSerdes<T> implements Serde<Observable<T>> {

    private final Serde<T> serde;

    private ObservableSerdes(Serde<T> serde) {
        this.serde = serde;
    }

    public static <T> ObservableSerdes<T> newObservableSerdes(Serde<T> serde
            , Function<T, Long> eventTimestampSupplier
            , Function<T, Long> ingestionTimestampSupplier) {
        return new ObservableSerdes<>(serde);
    }

    @Override
    public Serializer<Observable<T>> serializer() {
        return ObservableSerializer.newObservableSerializer(serde.serializer());
    }

    @Override
    public Deserializer<Observable<T>> deserializer() {
        return ObservableDeserializer.newObservableDeserializer(serde.deserializer());
    }
}

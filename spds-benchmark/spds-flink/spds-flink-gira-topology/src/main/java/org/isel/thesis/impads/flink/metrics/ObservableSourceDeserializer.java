package org.isel.thesis.impads.flink.metrics;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.metrics.api.SerializableEventTimestampAssigner;

import java.io.IOException;
import java.time.Instant;

public class ObservableSourceDeserializer<T> implements DeserializationSchema<Observable<T>> {

    private static final long serialVersionUID = 1L;

    private final DeserializationSchema<T> deserializationSchema;
    private final SerializableEventTimestampAssigner<T> eventTimestampAssigner;

    private ObservableSourceDeserializer(final DeserializationSchema<T> deserializationSchema
            , SerializableEventTimestampAssigner<T> eventTimestampAssigner) {
        this.deserializationSchema = deserializationSchema;
        this.eventTimestampAssigner = eventTimestampAssigner;
    }

    public static <T> ObservableSourceDeserializer<T> appendObservable(final DeserializationSchema<T> deserializationSchema
            , final SerializableEventTimestampAssigner<T> eventTimestampAssigner) {
        return new ObservableSourceDeserializer<>(deserializationSchema
                , eventTimestampAssigner);
    }

    @Override
    public Observable<T> deserialize(byte[] bytes) throws IOException {
        T obj = deserializationSchema.deserialize(bytes);
        return  Observable.of(obj, eventTimestampAssigner.extractEventTimestamp(obj), Instant.now().toEpochMilli());
    }

    @Override
    public boolean isEndOfStream(Observable<T> t) {
        return false;
    }

    @Override
    public TypeInformation<Observable<T>> getProducedType() {
        return TypeInformation.of(new TypeHint<Observable<T>>(){});
    }
}

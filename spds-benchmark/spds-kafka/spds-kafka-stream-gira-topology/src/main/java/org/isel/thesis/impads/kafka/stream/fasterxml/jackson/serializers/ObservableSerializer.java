package org.isel.thesis.impads.kafka.stream.fasterxml.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.isel.thesis.impads.metrics.Observable;

import java.io.IOException;

public abstract class ObservableSerializer<T> extends StdSerializer<Observable<T>> {

    public ObservableSerializer() {
        this(null);
    }

    public ObservableSerializer(Class<T> vc) {
        super((Class<Observable<T>>) vc);
    }

    @Override
    public void serialize(Observable iMeasureWrapper, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("event_timestamp", iMeasureWrapper.getEventTimestamp());
        jsonGenerator.writeNumberField("ingestion_timestamp", iMeasureWrapper.getIngestionTimestamp());
        jsonGenerator.writeNumberField("processed_timestamp", iMeasureWrapper.getProcessedTimestamp());
        jsonGenerator.writeFieldName("data");
        jsonGenerator.writeObject(iMeasureWrapper.getData());
        jsonGenerator.writeEndObject();
    }
}

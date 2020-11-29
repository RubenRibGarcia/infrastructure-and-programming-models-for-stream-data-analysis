package org.isel.thesis.impads.kafka.stream.fasterxml.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableGiraTravelsWithWazeResults;

import java.io.IOException;

public class ObservableGiraTravelsWithWazeResultSerializer extends StdSerializer<ObservableGiraTravelsWithWazeResults> {

    public ObservableGiraTravelsWithWazeResultSerializer() {
        this(null);
    }

    public ObservableGiraTravelsWithWazeResultSerializer(Class<ObservableGiraTravelsWithWazeResults> vc) {
        super(vc);
    }

    @Override
    public void serialize(ObservableGiraTravelsWithWazeResults model
            , JsonGenerator jsonGenerator
            , SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("event_timestamp", model.getEventTimestamp());
        jsonGenerator.writeNumberField("ingestion_timestamp", model.getIngestionTimestamp());
        jsonGenerator.writeNumberField("processed_timestamp", model.getProcessedTimestamp());
        jsonGenerator.writeFieldName("data");
        jsonGenerator.writeObject(model.getData());
        jsonGenerator.writeEndObject();
    }
}

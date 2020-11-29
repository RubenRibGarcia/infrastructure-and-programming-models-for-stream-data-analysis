package org.isel.thesis.impads.kafka.stream.fasterxml.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWaze;

import java.io.IOException;

public class ObservableJoinedGiraTravelsWithWazeSerializer extends StdSerializer<ObservableJoinedGiraTravelsWithWaze> {

    public ObservableJoinedGiraTravelsWithWazeSerializer() {
        this(null);
    }

    public ObservableJoinedGiraTravelsWithWazeSerializer(Class<ObservableJoinedGiraTravelsWithWaze> vc) {
        super(vc);
    }

    @Override
    public void serialize(ObservableJoinedGiraTravelsWithWaze model
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

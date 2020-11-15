package org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.metrics.Observable;

import java.io.IOException;

public class ObservableSimplifiedWazeIrregularitiesDeserializer extends StdDeserializer<ObservableSimplifiedWazeIrregularitiesModel> {

    public ObservableSimplifiedWazeIrregularitiesDeserializer() {
        this(null);
    }

    protected ObservableSimplifiedWazeIrregularitiesDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ObservableSimplifiedWazeIrregularitiesModel deserialize(JsonParser jsonParser
            , DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        long eventTimestamp = root.get("event_timestamp").asLong();
        long ingestionTimestamp = root.get("ingestion_timestamp").asLong();
        long processedTimestamp = root.get("processed_timestamp").asLong();

        JsonNode data = root.get("data");
        SimplifiedWazeIrregularitiesModel model = new SimplifiedWazeIrregularitiesModel(data.get("identifier").asText()
                , data.get("geometry").asText()
                , data.get("eventTimestamp").asLong());

        return new ObservableSimplifiedWazeIrregularitiesModel(Observable.of(model, eventTimestamp, ingestionTimestamp, processedTimestamp));
    }

}

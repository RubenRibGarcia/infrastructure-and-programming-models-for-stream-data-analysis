package org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.metrics.Observable;

import java.io.IOException;

public class ObservableSimplifiedGiraTravelsDeserializer extends StdDeserializer<ObservableSimplifiedGiraTravelsModel> {

    public ObservableSimplifiedGiraTravelsDeserializer() {
        this(null);
    }

    protected ObservableSimplifiedGiraTravelsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ObservableSimplifiedGiraTravelsModel deserialize(JsonParser jsonParser
            , DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        long eventTimestamp = root.get("event_timestamp").asLong();
        long ingestionTimestamp = root.get("ingestion_timestamp").asLong();
        long processedTimestamp = root.get("processed_timestamp").asLong();

        JsonNode data = root.get("data");
        SimplifiedGiraTravelsModel model = new SimplifiedGiraTravelsModel(data.get("identifier").asText()
                , data.get("geometry").asText()
                , data.get("eventTimestamp").asLong());

        return new ObservableSimplifiedGiraTravelsModel(Observable.of(model, eventTimestamp, ingestionTimestamp, processedTimestamp));
    }

}

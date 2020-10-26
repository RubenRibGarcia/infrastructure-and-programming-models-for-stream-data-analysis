package org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.isel.thesis.impads.kafka.stream.data.structures.Tuple2;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeJams;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.ObservableImpl;

import java.io.IOException;

public class ObservableJoinedGiraTravelsWithWazeJamsDeserializer extends StdDeserializer<ObservableJoinedGiraTravelsWithWazeJams> {

    public ObservableJoinedGiraTravelsWithWazeJamsDeserializer() {
        this(null);
    }

    protected ObservableJoinedGiraTravelsWithWazeJamsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ObservableJoinedGiraTravelsWithWazeJams deserialize(JsonParser jsonParser
            , DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        long eventTimestamp = root.get("event_timestamp").asLong();
        long ingestionTimestamp = root.get("ingestion_timestamp").asLong();
        long processedTimestamp = root.get("processed_timestamp").asLong();

        JsonNode data = root.get("data");

        JsonNode first = data.get("first");
        SimplifiedGiraTravelsModel giraTravelsModel = new SimplifiedGiraTravelsModel(first.get("identifier").asText()
                , first.get("geometry").asText()
                , first.get("eventTimestamp").asLong());

        JsonNode second = data.get("second");
        SimplifiedWazeJamsModel wazeJamsModel = new SimplifiedWazeJamsModel(second.get("identifier").asText()
                , second.get("geometry").asText()
                , second.get("eventTimestamp").asLong());

        return new ObservableJoinedGiraTravelsWithWazeJams(
                ObservableImpl.of(Tuple2.of(giraTravelsModel, wazeJamsModel), eventTimestamp, ingestionTimestamp, processedTimestamp));
    }

}

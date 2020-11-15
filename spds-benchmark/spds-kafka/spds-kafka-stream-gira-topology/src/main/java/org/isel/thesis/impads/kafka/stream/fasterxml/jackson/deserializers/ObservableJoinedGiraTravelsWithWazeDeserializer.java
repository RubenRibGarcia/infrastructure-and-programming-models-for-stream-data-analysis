package org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.isel.thesis.impads.kafka.stream.data.structures.Tuple3;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWaze;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.Observable;

import java.io.IOException;

public class ObservableJoinedGiraTravelsWithWazeDeserializer extends StdDeserializer<ObservableJoinedGiraTravelsWithWaze> {

    public ObservableJoinedGiraTravelsWithWazeDeserializer() {
        this(null);
    }

    protected ObservableJoinedGiraTravelsWithWazeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ObservableJoinedGiraTravelsWithWaze deserialize(JsonParser jsonParser
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

        JsonNode third = data.get("third");
        SimplifiedWazeIrregularitiesModel wazeIrregularitiesModel = new SimplifiedWazeIrregularitiesModel(third.get("identifier").asText()
                , third.get("geometry").asText()
                , third.get("eventTimestamp").asLong());

        return new ObservableJoinedGiraTravelsWithWaze(
                Observable.of(Tuple3.of(giraTravelsModel, wazeJamsModel, wazeIrregularitiesModel), eventTimestamp, ingestionTimestamp, processedTimestamp));
    }

}

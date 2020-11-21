package org.isel.thesis.impads.storm.low_level.topology.bolts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.low_level.topology.TupleMapper;
import org.isel.thesis.impads.storm.low_level.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.storm.low_level.topology.models.WazeJamsSourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class WazeJamsParserProcessor implements ParserProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WazeJamsParserProcessor.class);

    private final ObjectMapper mapper;
    private final TupleMapper<Observable<WazeJamsSourceModel>> tupleMapper;

    public WazeJamsParserProcessor(ObjectMapper mapper
            , TupleMapper<Observable<WazeJamsSourceModel>> tupleMapper) {
        this.mapper = mapper;
        this.tupleMapper = tupleMapper;
    }

    @Override
    public void process(Tuple tuple, OutputCollector collector) {
        Observable<WazeJamsSourceModel> model = tupleMapper.apply(tuple);

        if (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()) {

            Observable<SimplifiedWazeJamsModel> rvalue = model.map(new SimplifiedWazeJamsModel(model.getData().getId().toString()
                    , model.getData().getGeometry()
                    , model.getEventTimestamp()));

            try {
                Values values = new Values(Instant.ofEpochMilli(model.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                        , model.getEventTimestamp()
                        , mapper.writeValueAsString(rvalue));

                logger.info("Emiting Waze Jams {}", values);

                collector.emit(values);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public Fields outputFields() {
        return new Fields("key", "event_timestamp" ,"value");
    }
}

package org.isel.thesis.impads.storm.low_level.topology.bolts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.low_level.topology.TupleMapper;
import org.isel.thesis.impads.storm.low_level.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.storm.low_level.topology.models.WazeIrregularitiesSourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class WazeIrregularitiesParserProcessor implements ParserProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WazeIrregularitiesParserProcessor.class);

    private final ObjectMapper mapper;
    private final TupleMapper<Observable<WazeIrregularitiesSourceModel>> tupleMapper;

    public WazeIrregularitiesParserProcessor(ObjectMapper mapper
            , TupleMapper<Observable<WazeIrregularitiesSourceModel>> tupleMapper) {
        this.mapper = mapper;
        this.tupleMapper = tupleMapper;
    }

    @Override
    public void process(Tuple tuple, OutputCollector collector) {
        Observable<WazeIrregularitiesSourceModel> model = tupleMapper.apply(tuple);

        if (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()) {

            Observable<SimplifiedWazeIrregularitiesModel> rvalue = model.map(new SimplifiedWazeIrregularitiesModel(model.getData().getId().toString()
                    , model.getData().getGeometry()
                    , model.getEventTimestamp()));

            try {
                Values values = new Values(Instant.ofEpochMilli(model.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                        , model.getEventTimestamp()
                        , mapper.writeValueAsString(rvalue));

                logger.info("Emiting Waze Irregularities {}", values);

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

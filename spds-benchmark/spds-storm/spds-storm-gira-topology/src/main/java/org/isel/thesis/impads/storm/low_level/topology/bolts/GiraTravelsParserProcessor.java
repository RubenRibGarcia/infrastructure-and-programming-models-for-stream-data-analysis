package org.isel.thesis.impads.storm.low_level.topology.bolts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.low_level.topology.TupleMapper;
import org.isel.thesis.impads.storm.low_level.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.storm.low_level.topology.models.SimplifiedGiraTravelsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GiraTravelsParserProcessor implements ParserProcessor {

    private static final Logger logger = LoggerFactory.getLogger(GiraTravelsParserProcessor.class);

    private final ObjectMapper mapper;
    private final TupleMapper<Observable<GiraTravelsSourceModel>> tupleMapper;

    public GiraTravelsParserProcessor(ObjectMapper mapper
            , TupleMapper<Observable<GiraTravelsSourceModel>> tupleMapper) {
        this.mapper = mapper;
        this.tupleMapper = tupleMapper;
    }

    @Override
    public void process(Tuple tuple, OutputCollector collector) {
        Observable<GiraTravelsSourceModel> model = tupleMapper.apply(tuple);

        if (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()
                || model.getData().getNumberOfVertices() != null && model.getData().getNumberOfVertices() > 1
                || model.getData().getDistance() != null && model.getData().getDistance() > 0) {

            Observable<SimplifiedGiraTravelsModel> rvalue = model.map(new SimplifiedGiraTravelsModel(model.getData().getId().toString()
                    , model.getData().getGeometry()
                    , model.getEventTimestamp()));

            try {
                Values values = new Values(Instant.ofEpochMilli(model.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                        , model.getEventTimestamp()
                        , mapper.writeValueAsString(rvalue));

                logger.info("Emiting Gira Travels {}", values);

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

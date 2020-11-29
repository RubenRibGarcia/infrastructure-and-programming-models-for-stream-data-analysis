package org.isel.thesis.impads.storm.topology.bolts.processor;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.storm.topology.models.WazeIrregularitiesSourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class WazeIrregularitiesParserProcessor implements ParserProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WazeIrregularitiesParserProcessor.class);

    @Override
    public void process(Tuple tuple, OutputCollector collector) {
        Observable<WazeIrregularitiesSourceModel> model = (Observable<WazeIrregularitiesSourceModel>) tuple.getValueByField("value");

        if (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()) {

            Observable<SimplifiedWazeIrregularitiesModel> rvalue =
                    model.map(new SimplifiedWazeIrregularitiesModel(model.getData().getId().toString()
                    , model.getData().getGeometry()
                    , model.getEventTimestamp()));

            Values values = new Values(Instant.ofEpochMilli(model.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                    , model.getEventTimestamp()
                    , rvalue);

            collector.emit(values);
        }
    }

    @Override
    public Fields outputFields() {
        return new Fields("key", "event_timestamp" ,"value");
    }
}

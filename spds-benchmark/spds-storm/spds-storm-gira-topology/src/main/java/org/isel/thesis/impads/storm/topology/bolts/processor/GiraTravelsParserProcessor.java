package org.isel.thesis.impads.storm.topology.bolts.processor;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedGiraTravelsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GiraTravelsParserProcessor implements ParserProcessor {

    private static final Logger logger = LoggerFactory.getLogger(GiraTravelsParserProcessor.class);

    @Override
    public void process(Tuple tuple, OutputCollector collector) {
        Observable<GiraTravelsSourceModel> model = (Observable<GiraTravelsSourceModel>) tuple.getValueByField("value");

        if (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()
                || model.getData().getNumberOfVertices() != null && model.getData().getNumberOfVertices() > 1
                || model.getData().getDistance() != null && model.getData().getDistance() > 0) {

            Observable<SimplifiedGiraTravelsModel> rvalue =
                    model.map(new SimplifiedGiraTravelsModel(model.getData().getId().toString()
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

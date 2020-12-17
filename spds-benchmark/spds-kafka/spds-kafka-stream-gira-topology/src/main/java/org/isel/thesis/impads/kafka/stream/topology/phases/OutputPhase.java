package org.isel.thesis.impads.kafka.stream.topology.phases;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableGiraTravelsWithWazeResults;
import org.isel.thesis.impads.kafka.stream.topology.utils.SerdesUtils;

import java.io.Serializable;

public class OutputPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final KafkaStreamObservableMetricsCollector collector;
    private final ObjectMapper mapper;

    public OutputPhase(final KafkaStreamObservableMetricsCollector collector
            , final ObjectMapper mapper
            , final ResultPhase resultPhase) {
        this.collector = collector;
        this.mapper = mapper;

        initializePhase(resultPhase);
    }

    private void initializePhase(ResultPhase finalTransformPhase) {
        output(finalTransformPhase.getResultStream());
    }

    private void output(KStream<Long, ObservableGiraTravelsWithWazeResults> resultStream) {
        resultStream.peek((k,v) -> collector.collect(v));
        resultStream.to("kafka_result", Produced.with(Serdes.Long(), SerdesUtils.giraTravelsWithWazeResultsJsonSerdes(mapper)));
    }

}

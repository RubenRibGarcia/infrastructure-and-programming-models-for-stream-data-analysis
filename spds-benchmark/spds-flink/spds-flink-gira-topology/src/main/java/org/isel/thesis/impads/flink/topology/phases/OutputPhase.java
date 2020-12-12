package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.mapper.json.RPushJsonMapper;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.metrics.Observable;

import java.io.Serializable;

public class OutputPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConfigurationContainer configurationContainer;
    private final ObjectMapper mapper;

    public OutputPhase(final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper
            , final ResultPhase resultPhase) {
        this.configurationContainer = configurationContainer;
        this.mapper = mapper;

        initializePhase(resultPhase);
    }

    private void initializePhase(ResultPhase finalTransformPhase) {
        output(finalTransformPhase.getResultStream());
    }

    private void output(DataStream<Observable<GiraTravelsWithWazeAndIpmaResult>> resultStream) {

        resultStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()
                , new RedisSink<>(configurationContainer.getRedisConfiguration()
                        , new RPushJsonMapper<>(mapper, "flink_output"))));
    }

}

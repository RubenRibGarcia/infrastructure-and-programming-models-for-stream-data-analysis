package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.isel.thesis.impads.connectors.redis.RedisSinkFunction;
import org.isel.thesis.impads.connectors.redis.container.RedisWriteCommandsContainer;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.function.RedisSink;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.metrics.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class OutputPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(OutputPhase.class);

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

        RedisSinkFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> redisSinkFunction = RedisSinkFunction
                .sink(configurationContainer.getRedisConfiguration(), this::doOutput);

        resultStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()
                , RedisSink.sink(redisSinkFunction)));
    }

    private void doOutput(final RedisWriteCommandsContainer redisCommandsContainer
            , final Observable<GiraTravelsWithWazeAndIpmaResult> result) {

        try {
            String rvalue = mapper.writeValueAsString(result);
            redisCommandsContainer.lpush("flink_ouput", rvalue);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}

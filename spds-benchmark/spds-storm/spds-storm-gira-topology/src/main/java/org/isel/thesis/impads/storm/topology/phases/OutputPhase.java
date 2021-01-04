package org.isel.thesis.impads.storm.topology.phases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.topology.TopologyBuilder;
import org.isel.thesis.impads.connectors.redis.RedisWriterFunction;
import org.isel.thesis.impads.connectors.redis.container.RedisWriteCommandsContainer;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.topology.ConfigurationContainer;
import org.isel.thesis.impads.storm.topology.bolts.ResultSinkBolt;
import org.isel.thesis.impads.storm.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class OutputPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(OutputPhase.class);

    private transient final TopologyBuilder topologyBuilder;
    private final ConfigurationContainer configurationContainer;
    private final ObjectMapper mapper;

    public OutputPhase(final TopologyBuilder topologyBuilder
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper
            , final ResultPhase resultPhase) {
        this.topologyBuilder = topologyBuilder;
        this.configurationContainer = configurationContainer;
        this.mapper = mapper;

        initializePhase(resultPhase);
    }

    private void initializePhase(ResultPhase finalTransformPhase) {
        output(finalTransformPhase.getResultStream());
    }

    private void output(String resultStream) {

        RedisWriterFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> redisSinkFunction = RedisWriterFunction
                .newWriter(configurationContainer.getRedisConfiguration(), this::doOutput);

        ResultSinkBolt bolt = new ResultSinkBolt(t -> (Observable<GiraTravelsWithWazeAndIpmaResult>) t.getValueByField("value")
                , redisSinkFunction) ;

        topologyBuilder.setBolt("output", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration(), bolt), configurationContainer.getTopologyConfiguration().getParallelism())
                .shuffleGrouping(resultStream);
    }

    private void doOutput(final RedisWriteCommandsContainer redisCommandsContainer
            , final Observable<GiraTravelsWithWazeAndIpmaResult> result) {

        try {
            String rvalue = mapper.writeValueAsString(result);
            redisCommandsContainer.lpush("storm_output", rvalue);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}

package org.isel.thesis.impads.storm.topology.phases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.topology.TopologyBuilder;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.redis.bolt.RedisBoltBuilder;
import org.isel.thesis.impads.storm.redis.bolt.RedisStoreBolt;
import org.isel.thesis.impads.storm.topology.ConfigurationContainer;
import org.isel.thesis.impads.storm.topology.models.GiraTravelsWithWazeAndIpmaResult;

import java.io.Serializable;

public class OutputPhase implements Serializable {

    private static final long serialVersionUID = 1L;

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
        RedisStoreBolt<Observable<GiraTravelsWithWazeAndIpmaResult>> resultBolt =
                RedisBoltBuilder.<Observable<GiraTravelsWithWazeAndIpmaResult>>store(configurationContainer.getRedisConfiguration())
                        .tupleMapper(t -> (Observable<GiraTravelsWithWazeAndIpmaResult>) t.getValueByField("value"))
                        .consume((container, t) -> {
                            try {
                                container.rpush("storm_output", mapper.writeValueAsString(t));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        })
                        .build();

        topologyBuilder.setBolt("output", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration(), resultBolt))
                .shuffleGrouping(resultStream);
    }

}

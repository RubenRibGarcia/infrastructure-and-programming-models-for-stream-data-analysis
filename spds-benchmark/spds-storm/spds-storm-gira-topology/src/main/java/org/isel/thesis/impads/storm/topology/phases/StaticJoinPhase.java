package org.isel.thesis.impads.storm.topology.phases;

import org.apache.storm.topology.TopologyBuilder;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;
import org.isel.thesis.impads.connectors.redis.api.RedisKeyHashField;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.topology.ConfigurationContainer;
import org.isel.thesis.impads.storm.topology.bolts.IpmaValuesCacheableMapBolt;
import org.isel.thesis.impads.storm.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.storm.topology.utils.IpmaUtils;
import org.isel.thesis.impads.structures.Tuple3;

import java.io.Serializable;
import java.time.Instant;

public class StaticJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String  JOINED_GIRA_TRAVELS_WITH_WAZE_AND_IPMA_STREAM = "joined_gira_travels_with_waze_and_ipma";

    private final TopologyBuilder topologyBuilder;
    private final ConfigurationContainer configurationContainer;

    public StaticJoinPhase(final TopologyBuilder topologyBuilder
            , final ConfigurationContainer configurationContainer
            , final SecondJoinPhase secondJoinPhase) {
        this.topologyBuilder = topologyBuilder;
        this.configurationContainer = configurationContainer;

        initializePhase(secondJoinPhase);
    }

    private void initializePhase(SecondJoinPhase secondJoinPhase) {
        enrichJoinGiraTravelWithWazeWithIpma(secondJoinPhase.getJoinedGiraTravelsWithWazeStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.STATIC_JOIN) {
            topologyBuilder.setBolt("observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(JOINED_GIRA_TRAVELS_WITH_WAZE_AND_IPMA_STREAM);
        }
    }

    private void enrichJoinGiraTravelWithWazeWithIpma(String joinedGiraTravelsWithWazeStream) {

        RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> mapFunction =
            RedisHashCacheableMapFunction.newMapperMonitored(configurationContainer.getRedisConfiguration()
                    , key -> {
                        String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(key.getEventTimestamp()));
                        return RedisKeyHashField.of("ipma_sensores_values" , hashField);
                    }
                    , (readCommands, keyHashField, tuple) -> IpmaValuesModel.fetchAndAddFromRedis(keyHashField.getHashField(), readCommands)
                    , configurationContainer.getMetricsCollectorConfiguration());

        IpmaValuesCacheableMapBolt bolt = new IpmaValuesCacheableMapBolt(mapFunction
                , t -> (Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>) t.getValueByField("value")
                , "value");

        topologyBuilder.setBolt(JOINED_GIRA_TRAVELS_WITH_WAZE_AND_IPMA_STREAM
                , bolt
                , configurationContainer.getTopologyConfiguration().getParallelism())
                .shuffleGrouping(joinedGiraTravelsWithWazeStream);
    }

    public String getJoinedGiraTravelsWithWazeAndIpmaStream() {
        return JOINED_GIRA_TRAVELS_WITH_WAZE_AND_IPMA_STREAM;
    }
}

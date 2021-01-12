package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;
import org.isel.thesis.impads.connectors.redis.api.RedisKeyHashField;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.function.IpmaValuesCacheableMapFunction;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.flink.topology.utils.IpmaUtils;
import org.isel.thesis.impads.metrics.Observable;

import java.io.Serializable;
import java.time.Instant;

public class StaticJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConfigurationContainer configurationContainer;

    private DataStream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> enrichedJoinedGiraTravelsWithWazeAndIpma;

    public StaticJoinPhase(final ConfigurationContainer configurationContainer
            , SecondJoinPhase secondJoinPhase) {
        this.configurationContainer = configurationContainer;

        initializePhase(secondJoinPhase);
    }

    private void initializePhase(SecondJoinPhase secondJoinPhase) {
        this.enrichedJoinedGiraTravelsWithWazeAndIpma =
                enrichJoinGiraTravelWithWazeWithIpma(secondJoinPhase.getJoinedGiraTravelsWithWazeStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.STATIC_JOIN) {
            enrichedJoinedGiraTravelsWithWazeAndIpma.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
        }
    }

    private DataStream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> enrichJoinGiraTravelWithWazeWithIpma(
            DataStream<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> joinedGiraTravelsWithWazeStream) {

        RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> mapper =
                RedisHashCacheableMapFunction.newMapperMonitored(configurationContainer.getRedisConfiguration()
                        , key -> {
                            String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(key.getEventTimestamp()));
                            return RedisKeyHashField.of("ipma_sensores_values" , hashField);
                        }
                        , (readCommands, keyHashField, tuple) -> IpmaValuesModel.fetchAndAddFromRedis(keyHashField.getHashField(), readCommands)
                        , configurationContainer.getMetricsCollectorConfiguration());

        return joinedGiraTravelsWithWazeStream
                .rebalance()
                .map(IpmaValuesCacheableMapFunction.function(mapper));
    }

    public DataStream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> getEnrichedJoinedGiraTravelsWithWazeAndIpma() {
        return enrichedJoinedGiraTravelsWithWazeAndIpma;
    }
}

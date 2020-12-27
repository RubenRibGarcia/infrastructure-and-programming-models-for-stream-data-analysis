package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.function.CacheableRedisIpmaValuesFunction;
import org.isel.thesis.impads.flink.topology.function.RedisIpmaValuesFunction;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.Observable;

import java.io.Serializable;

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

        return joinedGiraTravelsWithWazeStream
                .process(new RedisIpmaValuesFunction(configurationContainer.getRedisConfiguration()));
    }

    public DataStream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> getEnrichedJoinedGiraTravelsWithWazeAndIpma() {
        return enrichedJoinedGiraTravelsWithWazeAndIpma;
    }
}

package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.redis.RedisProcessFunction;
import org.apache.flink.util.Collector;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
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

        if (untilPhase == Phases.INITIAL_TRANSFORMATION) {
            enrichedJoinedGiraTravelsWithWazeAndIpma.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
        }
    }

    private DataStream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> enrichJoinGiraTravelWithWazeWithIpma(
            DataStream<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> joinedGiraTravelsWithWazeStream) {

        return joinedGiraTravelsWithWazeStream
                .process(new RedisProcessFunction<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>, Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>>(configurationContainer.getRedisConfiguration()) {
                    @Override
                    public void processElement(Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> tuple
                            , Context context
                            , Collector<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> collector)
                            throws Exception {
                        String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(tuple.getData().f0.getEventTimestamp()));
                        IpmaValuesModel rvalue = IpmaValuesModel.fetchAndAddFromRedis(hashField, redisCommandsContainer);

                        collector.collect(tuple.map(Tuple4.of(tuple.getData().f0, tuple.getData().f1, tuple.getData().f2, rvalue)));
                    }
                });
    }

    public DataStream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> getEnrichedJoinedGiraTravelsWithWazeAndIpma() {
        return enrichedJoinedGiraTravelsWithWazeAndIpma;
    }
}

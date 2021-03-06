package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.Observable;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class FirstJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConfigurationContainer configurationContainer;

    private DataStream<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> joinedGiraTravelsWithWazeJamsStream;

    public FirstJoinPhase(final ConfigurationContainer configurationContainer
            , final ParsePhase parsePhase) {
        this.configurationContainer = configurationContainer;

        initializePhase(parsePhase);
    }

    private void initializePhase(final ParsePhase parsePhase) {
        this.joinedGiraTravelsWithWazeJamsStream = joinGiraTravelsWithWazeJams(parsePhase.getSimplifiedGiraTravelsStream()
                , parsePhase.getSimplifiedWazeJamsStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.FIRST_JOIN) {
            joinedGiraTravelsWithWazeJamsStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
        }
    }

    private DataStream<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> joinGiraTravelsWithWazeJams(
            DataStream<Observable<SimplifiedGiraTravelsModel>> simplifiedGiraTravelsStream
            , DataStream<Observable<SimplifiedWazeJamsModel>> simplifiedWazeJamsStream) {

        return simplifiedGiraTravelsStream
                .join(simplifiedWazeJamsStream)
                .where((KeySelector<Observable<SimplifiedGiraTravelsModel>, Long>) tuple -> Instant.ofEpochMilli(tuple.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                .equalTo((KeySelector<Observable<SimplifiedWazeJamsModel>, Long>) tuple -> Instant.ofEpochMilli(tuple.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                .window(SlidingEventTimeWindows.of(Time.milliseconds(5), Time.milliseconds(5)))
                .apply(new JoinFunction<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>, Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>>() {
                    @Override
                    public Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> join(
                            Observable<SimplifiedGiraTravelsModel> left
                            , Observable<SimplifiedWazeJamsModel> right) throws Exception {

                        final Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel> tuple =
                                Tuple2.of(left.getData(), right.getData());

                        return left.join(tuple, right);
                    }
                });
    }

    public DataStream<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> getJoinedGiraTravelsWithWazeJamsStream() {
        return joinedGiraTravelsWithWazeJamsStream;
    }
}

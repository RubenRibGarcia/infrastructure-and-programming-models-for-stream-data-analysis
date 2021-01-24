package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.Observable;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SecondJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConfigurationContainer configurationContainer;

    private DataStream<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> joinedGiraTravelsWithWazeStream;

    public SecondJoinPhase(final ConfigurationContainer configurationContainer
            , final ParsePhase parsePhase
            , final FirstJoinPhase firstJoinPhase) {
        this.configurationContainer = configurationContainer;

        this.initializePhase(parsePhase
                , firstJoinPhase);
    }

    private void initializePhase(ParsePhase parsePhase
            , FirstJoinPhase firstJoinPhase) {

        this.joinedGiraTravelsWithWazeStream =
                joinGiraTravelsWithWazeJamsWithWazeIrregularities(firstJoinPhase.getJoinedGiraTravelsWithWazeJamsStream()
                        , parsePhase.getSimplifiedWazeIrregularitiesStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.SECOND_JOIN) {
            joinedGiraTravelsWithWazeStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
        }
    }

    private DataStream<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> joinGiraTravelsWithWazeJamsWithWazeIrregularities(
            DataStream<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> joinedGiraTravelsWithWazeJamsStream
            , DataStream<Observable<SimplifiedWazeIrregularitiesModel>> simplifiedWazeIrregularitiesStream) {

        return joinedGiraTravelsWithWazeJamsStream
                .join(simplifiedWazeIrregularitiesStream)
                .where(k -> Instant.ofEpochMilli(k.getData().f0.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                .equalTo(k -> Instant.ofEpochMilli(k.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                .window(SlidingEventTimeWindows.of(Time.milliseconds(5), Time.milliseconds(5)))
                .apply(new JoinFunction<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>, Observable<SimplifiedWazeIrregularitiesModel>, Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>>() {
                    @Override
                    public Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> join(
                            Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> left
                            , Observable<SimplifiedWazeIrregularitiesModel> right) throws Exception {

                        final Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel> tuple =
                                Tuple3.of(left.getData().f0, left.getData().f1, right.getData());

                        return left.join(tuple, right);
                    }
                });
    }

    public DataStream<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> getJoinedGiraTravelsWithWazeStream() {
        return joinedGiraTravelsWithWazeStream;
    }
}

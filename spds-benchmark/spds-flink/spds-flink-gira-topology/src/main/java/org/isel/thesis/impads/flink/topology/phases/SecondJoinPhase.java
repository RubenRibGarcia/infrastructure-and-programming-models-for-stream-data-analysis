package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
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
            , final InitialTransformationPhase initialTransformationPhase
            , final FirstJoinPhase firstJoinPhase) {
        this.configurationContainer = configurationContainer;

        this.initializePhase(initialTransformationPhase
                , firstJoinPhase);
    }

    private void initializePhase(InitialTransformationPhase parsePhase
            , FirstJoinPhase firstJoinPhase) {

        this.joinedGiraTravelsWithWazeStream =
                joinGiraTravelsWithWazeJamsWithWazeIrregularities(firstJoinPhase.getJoinedGiraTravelsWithWazeJamsStream()
                        , parsePhase.getSimplifiedWazeIrregularitiesStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.INITIAL_TRANSFORMATION) {
            joinedGiraTravelsWithWazeStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
        }
    }

    private DataStream<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> joinGiraTravelsWithWazeJamsWithWazeIrregularities(
            DataStream<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> joinedGiraTravelsWithWazeJamsStream
            , DataStream<Observable<SimplifiedWazeIrregularitiesModel>> simplifiedWazeIrregularitiesStream) {

        return joinedGiraTravelsWithWazeJamsStream.keyBy(k -> Instant.ofEpochMilli(k.getData().f0.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                .intervalJoin(simplifiedWazeIrregularitiesStream.keyBy(k -> Instant.ofEpochMilli(k.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()))
                .between(Time.milliseconds(-5), Time.milliseconds(5))
                .process(new ProcessJoinFunction<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>, Observable<SimplifiedWazeIrregularitiesModel>, Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>>() {
                    @Override
                    public void processElement(Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> left
                            , Observable<SimplifiedWazeIrregularitiesModel> right
                            , Context context
                            , Collector<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> collector) throws Exception {

                        final Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel> tuple =
                                Tuple3.of(left.getData().f0, left.getData().f1, right.getData());

                        collector.collect(left.join(tuple, right));
                    }
                });
    }

    public DataStream<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> getJoinedGiraTravelsWithWazeStream() {
        return joinedGiraTravelsWithWazeStream;
    }
}

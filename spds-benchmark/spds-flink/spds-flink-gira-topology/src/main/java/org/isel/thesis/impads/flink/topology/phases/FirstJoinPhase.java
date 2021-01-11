package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
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
                .rebalance()
                .keyBy((KeySelector<Observable<SimplifiedGiraTravelsModel>, Long>) tuple -> Instant.ofEpochMilli(tuple.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                .intervalJoin(simplifiedWazeJamsStream.keyBy((KeySelector<Observable<SimplifiedWazeJamsModel>, Long>) tuple -> Instant.ofEpochMilli(tuple.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()))
                .between(Time.milliseconds(-5), Time.milliseconds(5))
                .process(new ProcessJoinFunction<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>, Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>>() {
                    @Override
                    public void processElement(Observable<SimplifiedGiraTravelsModel> left
                            , Observable<SimplifiedWazeJamsModel> right
                            , Context context
                            , Collector<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> collector) throws Exception {

                        final Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel> tuple =
                                Tuple2.of(left.getData(), right.getData());

                        collector.collect(left.join(tuple, right));
                    }
                });
    }

    public DataStream<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> getJoinedGiraTravelsWithWazeJamsStream() {
        return joinedGiraTravelsWithWazeJamsStream;
    }
}

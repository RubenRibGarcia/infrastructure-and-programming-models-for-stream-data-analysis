package org.isel.thesis.impads.storm.topology.phases;

import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.topology.ConfigurationContainer;
import org.isel.thesis.impads.storm.topology.bolts.join.KeySelector;
import org.isel.thesis.impads.storm.topology.bolts.join.ObservableJoinBolt;
import org.isel.thesis.impads.storm.topology.bolts.join.TupleFieldSelector;
import org.isel.thesis.impads.storm.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.structures.Tuple2;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class FirstJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String JOINED_GIRA_TRAVELS_WITH_WAZE_JAMS_STREAM = "joined_gira_travels_with_waze_jams";

    private final TopologyBuilder topologyBuilder;
    private final ConfigurationContainer configurationContainer;

    public FirstJoinPhase(final TopologyBuilder topologyBuilder
            , final ConfigurationContainer configurationContainer
            , final ParsePhase initialTransformationPhase) {
        this.topologyBuilder = topologyBuilder;
        this.configurationContainer = configurationContainer;

        initializePhase(initialTransformationPhase);
    }

    private void initializePhase(final ParsePhase parsePhase) {
        joinGiraTravelsWithWazeJams(parsePhase.getSimplifiedGiraTravelsStream()
                , parsePhase.getSimplifiedWazeJamsStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.FIRST_JOIN) {
            topologyBuilder.setBolt("observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(JOINED_GIRA_TRAVELS_WITH_WAZE_JAMS_STREAM);
        }
    }

    private void joinGiraTravelsWithWazeJams(String simplifiedGiraTravelsStream
            , String simplifiedWazeJamsStream) {

        ObservableJoinBolt<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>
                , Long> joinedGiraTravelsWithWazeJamsBolt = ObservableJoinBolt.JoinBuilder
                .<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>, Long>from(simplifiedGiraTravelsStream
                        , KeySelector.selector(fn -> Instant.ofEpochMilli(fn.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        , TupleFieldSelector.selector(tuple -> (Observable<SimplifiedGiraTravelsModel>) tuple.getValueByField("value")))
                .join(simplifiedWazeJamsStream
                        , KeySelector.selector(fn -> Instant.ofEpochMilli(fn.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        , TupleFieldSelector.selector(tuple -> (Observable<SimplifiedWazeJamsModel>) tuple.getValueByField("value")))
                .apply((from, join) -> {
                    Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> obs =
                            from.join(Tuple2.of(from.getData(), join.getData()), join);

                    Values values = new Values(Instant.ofEpochMilli(obs.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                            , obs.getData().getFirst().getEventTimestamp()
                            , obs);

                    return values;
                })
                .outputFields("key", "event_timestamp", "value")
                .build();

        topologyBuilder.setBolt(JOINED_GIRA_TRAVELS_WITH_WAZE_JAMS_STREAM, joinedGiraTravelsWithWazeJamsBolt
                .withWindow(BaseWindowedBolt.Duration.of(5), BaseWindowedBolt.Duration.of(5))
                .withTimestampField("event_timestamp")
                .withWatermarkInterval(BaseWindowedBolt.Duration.of(50)), configurationContainer.getTopologyConfiguration().getParallelism())
                .fieldsGrouping(simplifiedGiraTravelsStream, new Fields("key"))
                .fieldsGrouping(simplifiedWazeJamsStream, new Fields("key"));
    }

    public String getJoinedGiraTravelsWithWazeJamsStream() {
        return JOINED_GIRA_TRAVELS_WITH_WAZE_JAMS_STREAM;
    }
}

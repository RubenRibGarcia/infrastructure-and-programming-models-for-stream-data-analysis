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
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.structures.Tuple2;
import org.isel.thesis.impads.structures.Tuple3;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SecondJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String JOINED_GIRA_TRAVELS_WITH_WAZE_STREAM = "joined_gira_travels_with_waze";

    private final TopologyBuilder topologyBuilder;
    private final ConfigurationContainer configurationContainer;

    public SecondJoinPhase(final TopologyBuilder topologyBuilder
            , final ConfigurationContainer configurationContainer
            , final InitialTransformationPhase initialTransformationPhase
            , final FirstJoinPhase firstJoinPhase) {
        this.topologyBuilder = topologyBuilder;
        this.configurationContainer = configurationContainer;

        this.initializePhase(initialTransformationPhase
                , firstJoinPhase);
    }

    private void initializePhase(InitialTransformationPhase parsePhase
            , FirstJoinPhase firstJoinPhase) {

        joinGiraTravelsWithWazeJamsWithWazeIrregularities(firstJoinPhase.getJoinedGiraTravelsWithWazeJamsStream()
                , parsePhase.getSimplifiedWazeIrregularitiesStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.SECOND_JOIN) {
            topologyBuilder.setBolt("observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(JOINED_GIRA_TRAVELS_WITH_WAZE_STREAM);
        }
    }

    private void joinGiraTravelsWithWazeJamsWithWazeIrregularities(String joinedGiraTravelsWithWazeJamsStream
            , String simplifiedWazeIrregularitiesStream) {

        ObservableJoinBolt<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>, Observable<SimplifiedWazeIrregularitiesModel>
                , Long> joinedGiraTravelsWithWazeBolt = ObservableJoinBolt.JoinBuilder
                .<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>, Observable<SimplifiedWazeIrregularitiesModel>, Long>from(joinedGiraTravelsWithWazeJamsStream
                        , KeySelector.selector(fn -> Instant.ofEpochMilli(fn.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        , TupleFieldSelector.selector(tuple -> (Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>) tuple.getValueByField("value")))
                .join(simplifiedWazeIrregularitiesStream
                        , KeySelector.selector(fn -> Instant.ofEpochMilli(fn.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        , TupleFieldSelector.selector(tuple -> (Observable<SimplifiedWazeIrregularitiesModel>) tuple.getValueByField("value")))
                .apply((from, join) -> {
                    Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> obs =
                            from.join(Tuple3.of(from.getData().getFirst(), from.getData().getSecond(), join.getData()), join);

                    Values values = new Values(Instant.ofEpochMilli(obs.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                            , obs.getEventTimestamp()
                            , obs);

                    return values;
                })
                .outputFields("key", "event_timestamp", "value")
                .build();

        topologyBuilder.setBolt(JOINED_GIRA_TRAVELS_WITH_WAZE_STREAM, joinedGiraTravelsWithWazeBolt
                .withWindow(BaseWindowedBolt.Duration.of(30), BaseWindowedBolt.Duration.of(30))
                .withTimestampExtractor(tuple -> tuple.getLongByField("event_timestamp")))
                .fieldsGrouping(joinedGiraTravelsWithWazeJamsStream, new Fields("key"))
                .fieldsGrouping(simplifiedWazeIrregularitiesStream, new Fields("key"));
    }

    public String getJoinedGiraTravelsWithWazeStream() {
        return JOINED_GIRA_TRAVELS_WITH_WAZE_STREAM;
    }
}

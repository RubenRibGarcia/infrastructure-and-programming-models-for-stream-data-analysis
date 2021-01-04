package org.isel.thesis.impads.kafka.stream.topology.phases;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.topology.ConfigurationContainer;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWaze;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeJams;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.utils.SerdesUtils;
import org.isel.thesis.impads.structures.Tuple3;

import java.io.Serializable;
import java.time.Duration;

public class SecondJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConfigurationContainer configurationContainer;
    private final KafkaStreamObservableMetricsCollector collector;
    private final ObjectMapper mapper;

    private KStream<Long, ObservableJoinedGiraTravelsWithWaze> joinedGiraTravelsWithWazeStream;

    public SecondJoinPhase(final ConfigurationContainer configurationContainer
            , final KafkaStreamObservableMetricsCollector collector
            , final ObjectMapper mapper
            , final ParsePhase initialTransformationPhase
            , final FirstJoinPhase firstJoinPhase) {
        this.configurationContainer = configurationContainer;
        this.collector = collector;
        this.mapper = mapper;

        this.initializePhase(initialTransformationPhase
                , firstJoinPhase);
    }

    private void initializePhase(ParsePhase parsePhase
            , FirstJoinPhase firstJoinPhase) {

        this.joinedGiraTravelsWithWazeStream =
                joinGiraTravelsWithWazeJamsWithWazeIrregularities(firstJoinPhase.getJoinedGiraTravelsWithWazeJamsStream()
                        , parsePhase.getSimplifiedWazeIrregularitiesStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.SECOND_JOIN) {
            joinedGiraTravelsWithWazeStream.peek((k,v) -> collector.collect(v));
        }
    }

    private KStream<Long, ObservableJoinedGiraTravelsWithWaze> joinGiraTravelsWithWazeJamsWithWazeIrregularities(
            KStream<Long, ObservableJoinedGiraTravelsWithWazeJams> joinedGiraTravelsWithWazeJamsStream
            , KStream<Long, ObservableSimplifiedWazeIrregularitiesModel> simplifiedWazeIrregularitiesStream) {

        return joinedGiraTravelsWithWazeJamsStream
                .join(simplifiedWazeIrregularitiesStream
                        , (left, right) -> new ObservableJoinedGiraTravelsWithWaze(left.join(Tuple3.of(left.getData().getFirst(), left.getData().getSecond(), right.getData()), right))
                        , JoinWindows.of(Duration.ofMillis(5))
                        , StreamJoined.with(Serdes.Long()
                                , SerdesUtils.joinedGiraTravelsWithWazeJamsJsonSerdes(mapper)
                                , SerdesUtils.simplifiedWazeIrregularitiesSerdes(mapper)));
    }

    public KStream<Long, ObservableJoinedGiraTravelsWithWaze> getJoinedGiraTravelsWithWazeStream() {
        return joinedGiraTravelsWithWazeStream;
    }
}

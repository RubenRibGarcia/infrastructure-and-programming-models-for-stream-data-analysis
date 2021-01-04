package org.isel.thesis.impads.kafka.stream.topology.phases;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.topology.ConfigurationContainer;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeJams;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.utils.SerdesUtils;
import org.isel.thesis.impads.structures.Tuple2;

import java.io.Serializable;
import java.time.Duration;

public class FirstJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConfigurationContainer configurationContainer;
    private final KafkaStreamObservableMetricsCollector collector;
    private final ObjectMapper mapper;

    private KStream<Long, ObservableJoinedGiraTravelsWithWazeJams> joinedGiraTravelsWithWazeJamsStream;

    public FirstJoinPhase(final ConfigurationContainer configurationContainer
            , final KafkaStreamObservableMetricsCollector collector
            , final ObjectMapper mapper
            , final ParsePhase initialTransformationPhase) {
        this.configurationContainer = configurationContainer;
        this.collector = collector;
        this.mapper = mapper;

        initializePhase(initialTransformationPhase);
    }

    private void initializePhase(final ParsePhase parsePhase) {
        this.joinedGiraTravelsWithWazeJamsStream = joinGiraTravelsWithWazeJams(parsePhase.getSimplifiedGiraTravelsStream()
                , parsePhase.getSimplifiedWazeJamsStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.FIRST_JOIN) {
            joinedGiraTravelsWithWazeJamsStream.peek((k,v) -> collector.collect(v));
        }
    }

    private KStream<Long, ObservableJoinedGiraTravelsWithWazeJams> joinGiraTravelsWithWazeJams(
            KStream<Long, ObservableSimplifiedGiraTravelsModel> simplifiedGiraTravelsStream
            , KStream<Long, ObservableSimplifiedWazeJamsModel> simplifiedWazeJamsStream) {
        return simplifiedGiraTravelsStream
                .join(simplifiedWazeJamsStream
                        , (left, right) -> new ObservableJoinedGiraTravelsWithWazeJams(left.join(Tuple2.of(left.getData(), right.getData()), right))
                        , JoinWindows.of(Duration.ofMillis(5))
                        , StreamJoined.with(Serdes.Long()
                                , SerdesUtils.simplifiedGiraTravelsSerdes(mapper)
                                , SerdesUtils.simplifiedWazeJamsSerdes(mapper)));
    }

    public KStream<Long, ObservableJoinedGiraTravelsWithWazeJams> getJoinedGiraTravelsWithWazeJamsStream() {
        return joinedGiraTravelsWithWazeJamsStream;
    }
}

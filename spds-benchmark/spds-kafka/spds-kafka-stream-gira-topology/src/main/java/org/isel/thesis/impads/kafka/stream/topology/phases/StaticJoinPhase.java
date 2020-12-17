package org.isel.thesis.impads.kafka.stream.topology.phases;



import org.apache.kafka.streams.kstream.KStream;
import org.isel.thesis.impads.kafka.stream.connectors.redis.common.container.RedisCommandsContainer;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.topology.ConfigurationContainer;
import org.isel.thesis.impads.kafka.stream.topology.model.IpmaValuesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWaze;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeAndIpma;
import org.isel.thesis.impads.kafka.stream.topology.utils.IpmaUtils;
import org.isel.thesis.impads.structures.Tuple4;

import java.io.Serializable;
import java.time.Instant;

public class StaticJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConfigurationContainer configurationContainer;
    private final KafkaStreamObservableMetricsCollector collector;
    private final RedisCommandsContainer redisContainer;

    private KStream<Long, ObservableJoinedGiraTravelsWithWazeAndIpma> enrichedJoinedGiraTravelsWithWazeAndIpma;

    public StaticJoinPhase(final ConfigurationContainer configurationContainer
            , final KafkaStreamObservableMetricsCollector collector
            , final RedisCommandsContainer redisContainer
            , SecondJoinPhase secondJoinPhase) {
        this.configurationContainer = configurationContainer;
        this.collector = collector;
        this.redisContainer = redisContainer;

        initializePhase(secondJoinPhase);
    }

    private void initializePhase(SecondJoinPhase secondJoinPhase) {
        this.enrichedJoinedGiraTravelsWithWazeAndIpma =
                enrichJoinGiraTravelWithWazeWithIpma(secondJoinPhase.getJoinedGiraTravelsWithWazeStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.STATIC_JOIN) {
            enrichedJoinedGiraTravelsWithWazeAndIpma.peek((k,v) -> collector.collect(v));
        }
    }

    private KStream<Long, ObservableJoinedGiraTravelsWithWazeAndIpma> enrichJoinGiraTravelWithWazeWithIpma(
            KStream<Long, ObservableJoinedGiraTravelsWithWaze> joinedGiraTravelsWithWazeStream) {

        return joinedGiraTravelsWithWazeStream
                .mapValues(v -> {
                    String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(v.getData().getFirst().getEventTimestamp()));
                    IpmaValuesModel rvalue = IpmaValuesModel.fetchAndAddFromRedis(hashField, redisContainer);

                    return new ObservableJoinedGiraTravelsWithWazeAndIpma(
                            v.map(Tuple4.of(v.getData().getFirst(), v.getData().getSecond(), v.getData().getThird(), rvalue)));
                });
    }

    public KStream<Long, ObservableJoinedGiraTravelsWithWazeAndIpma> getEnrichedJoinedGiraTravelsWithWazeAndIpma() {
        return enrichedJoinedGiraTravelsWithWazeAndIpma;
    }
}

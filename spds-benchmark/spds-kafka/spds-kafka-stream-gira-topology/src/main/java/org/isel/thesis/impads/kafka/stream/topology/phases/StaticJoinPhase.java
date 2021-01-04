package org.isel.thesis.impads.kafka.stream.topology.phases;

import org.apache.kafka.streams.kstream.KStream;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;
import org.isel.thesis.impads.connectors.redis.api.RedisKeyHashField;
import org.isel.thesis.impads.kafka.stream.connectors.redis.KafkaStreamRedisHashCacheableMapValuesFunction;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.topology.ConfigurationContainer;
import org.isel.thesis.impads.kafka.stream.topology.function.IpmaValuesCacheableMapValuesFunction;
import org.isel.thesis.impads.kafka.stream.topology.model.IpmaValuesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWaze;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeAndIpma;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.utils.IpmaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;

public class StaticJoinPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(StaticJoinPhase.class);

    private final ConfigurationContainer configurationContainer;
    private final KafkaStreamObservableMetricsCollector collector;

    private KStream<Long, ObservableJoinedGiraTravelsWithWazeAndIpma> enrichedJoinedGiraTravelsWithWazeAndIpma;

    public StaticJoinPhase(final ConfigurationContainer configurationContainer
            , final KafkaStreamObservableMetricsCollector collector
            , final SecondJoinPhase secondJoinPhase) {
        this.configurationContainer = configurationContainer;
        this.collector = collector;

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

        RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> mapper =
                RedisHashCacheableMapFunction.newMapperMonitored(configurationContainer.getRedisConfiguration()
                        , key -> {
                            String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(key.getEventTimestamp()));
                            return RedisKeyHashField.of("ipma_sensores_values" , hashField);
                        }
                        , (readCommands, keyHashField, tuple) -> IpmaValuesModel.fetchAndAddFromRedis(keyHashField.getHashField(), readCommands)
                        , configurationContainer.getMetricsCollectorConfiguration());

        KafkaStreamRedisHashCacheableMapValuesFunction<ObservableJoinedGiraTravelsWithWaze, ObservableJoinedGiraTravelsWithWazeAndIpma, SimplifiedGiraTravelsModel, IpmaValuesModel> mapValuesFunction =
                new IpmaValuesCacheableMapValuesFunction(mapper);

        try {
            mapValuesFunction.open();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return joinedGiraTravelsWithWazeStream
                .mapValues(mapValuesFunction);
    }

    public KStream<Long, ObservableJoinedGiraTravelsWithWazeAndIpma> getEnrichedJoinedGiraTravelsWithWazeAndIpma() {
        return enrichedJoinedGiraTravelsWithWazeAndIpma;
    }
}

package org.isel.thesis.impads.kafka.stream.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.isel.thesis.impads.kafka.stream.connectors.redis.common.container.RedisCommandsContainer;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.topology.phases.FirstJoinPhase;
import org.isel.thesis.impads.kafka.stream.topology.phases.IngestionPhase;
import org.isel.thesis.impads.kafka.stream.topology.phases.InitialTransformationPhase;
import org.isel.thesis.impads.kafka.stream.topology.phases.OutputPhase;
import org.isel.thesis.impads.kafka.stream.topology.phases.Phases;
import org.isel.thesis.impads.kafka.stream.topology.phases.ResultPhase;
import org.isel.thesis.impads.kafka.stream.topology.phases.SecondJoinPhase;
import org.isel.thesis.impads.kafka.stream.topology.phases.StaticJoinPhase;
import org.locationtech.jts.geom.GeometryFactory;

public final class GiraTravelsTopologyBuilder {

    private final StreamsBuilder streamsBuilder;
    private final ConfigurationContainer configurationContainer;
    private final ObjectMapper mapper;
    private final GeometryFactory geoFactory;
    private final KafkaStreamObservableMetricsCollector collector;
    private final RedisCommandsContainer redisCommandsContainer;

    private GiraTravelsTopologyBuilder(final StreamsBuilder streamsBuilder
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper
            , final GeometryFactory geoFactory
            , final KafkaStreamObservableMetricsCollector collector
            , final RedisCommandsContainer redisCommandsContainer) {
        this.streamsBuilder = streamsBuilder;
        this.configurationContainer = configurationContainer;
        this.mapper = mapper;
        this.geoFactory = geoFactory;
        this.collector = collector;
        this.redisCommandsContainer = redisCommandsContainer;
    }

    public static final Topology build(final StreamsBuilder streamsBuilder
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper
            , final GeometryFactory geoFactory
            , final KafkaStreamObservableMetricsCollector collector
            , final RedisCommandsContainer redisCommandsContainer) {
        GiraTravelsTopologyBuilder builder =
                new GiraTravelsTopologyBuilder(streamsBuilder
                        , configurationContainer
                        , mapper
                        , geoFactory
                        , collector
                        , redisCommandsContainer);

        return builder.build();
    }

    private Topology build() {

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.INGESTION) {
            initializeIngestionPhase();
        }
        else if (untilPhase == Phases.INITIAL_TRANSFORMATION) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            initializeInitialTransformationPhase(ingestionPhase);
        }
        else if (untilPhase == Phases.FIRST_JOIN) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            InitialTransformationPhase initialTransformationPhase = initializeInitialTransformationPhase(ingestionPhase);
            initializeFirstJoinPhase(initialTransformationPhase);
        }
        else if (untilPhase == Phases.SECOND_JOIN){
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            InitialTransformationPhase initialTransformationPhase = initializeInitialTransformationPhase(ingestionPhase);
            FirstJoinPhase firstJoinPhase = initializeFirstJoinPhase(initialTransformationPhase);
            initializeSecondJoinPhase(initialTransformationPhase, firstJoinPhase);
        }
        else if (untilPhase == Phases.STATIC_JOIN) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            InitialTransformationPhase initialTransformationPhase = initializeInitialTransformationPhase(ingestionPhase);
            FirstJoinPhase firstJoinPhase = initializeFirstJoinPhase(initialTransformationPhase);
            SecondJoinPhase secondJoinPhase = initializeSecondJoinPhase(initialTransformationPhase, firstJoinPhase);
            initializeStaticJoinPhase(secondJoinPhase);
        }
        else if (untilPhase == Phases.RESULT) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            InitialTransformationPhase initialTransformationPhase = initializeInitialTransformationPhase(ingestionPhase);
            FirstJoinPhase firstJoinPhase = initializeFirstJoinPhase(initialTransformationPhase);
            SecondJoinPhase secondJoinPhase = initializeSecondJoinPhase(initialTransformationPhase, firstJoinPhase);
            StaticJoinPhase staticJoinPhase = initializeStaticJoinPhase(secondJoinPhase);
            initializeResultPhase(staticJoinPhase);
        }
        else if (untilPhase == Phases.OUTPUT) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            InitialTransformationPhase initialTransformationPhase = initializeInitialTransformationPhase(ingestionPhase);
            FirstJoinPhase firstJoinPhase = initializeFirstJoinPhase(initialTransformationPhase);
            SecondJoinPhase secondJoinPhase = initializeSecondJoinPhase(initialTransformationPhase, firstJoinPhase);
            StaticJoinPhase staticJoinPhase = initializeStaticJoinPhase(secondJoinPhase);
            ResultPhase resultPhase = initializeResultPhase(staticJoinPhase);
            initializeOutputPhase(resultPhase);
        }
        else {
            throw new IllegalArgumentException("Unknown " + untilPhase + " phase");
        }

        return streamsBuilder.build();
    }

    private IngestionPhase initializeIngestionPhase() {
        return new IngestionPhase(streamsBuilder, configurationContainer, mapper, collector);
    }

    private InitialTransformationPhase initializeInitialTransformationPhase(IngestionPhase ingestionPhase) {
        return new InitialTransformationPhase(configurationContainer, collector, ingestionPhase);
    }

    private FirstJoinPhase initializeFirstJoinPhase(InitialTransformationPhase initialTransformationPhase) {
        return new FirstJoinPhase(configurationContainer, collector, mapper, initialTransformationPhase);
    }

    private SecondJoinPhase initializeSecondJoinPhase(InitialTransformationPhase initialTransformationPhase
            , FirstJoinPhase firstJoinPhase) {
        return new SecondJoinPhase(configurationContainer, collector, mapper, initialTransformationPhase, firstJoinPhase);
    }

    private StaticJoinPhase initializeStaticJoinPhase(SecondJoinPhase secondJoinPhase) {
        return new StaticJoinPhase(configurationContainer, collector, redisCommandsContainer, secondJoinPhase);
    }

    private ResultPhase initializeResultPhase(StaticJoinPhase staticJoinPhase) {
        return new ResultPhase(configurationContainer, geoFactory, collector, staticJoinPhase);
    }

    private OutputPhase initializeOutputPhase(ResultPhase resultPhase) {
        return new OutputPhase(collector, mapper, resultPhase);
    }
}

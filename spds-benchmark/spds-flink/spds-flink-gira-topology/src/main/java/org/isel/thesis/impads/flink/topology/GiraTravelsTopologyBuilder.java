package org.isel.thesis.impads.flink.topology;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.isel.thesis.impads.flink.topology.phases.FirstJoinPhase;
import org.isel.thesis.impads.flink.topology.phases.IngestionPhase;
import org.isel.thesis.impads.flink.topology.phases.InitialTransformationPhase;
import org.isel.thesis.impads.flink.topology.phases.OutputPhase;
import org.isel.thesis.impads.flink.topology.phases.Phases;
import org.isel.thesis.impads.flink.topology.phases.ResultPhase;
import org.isel.thesis.impads.flink.topology.phases.SecondJoinPhase;
import org.isel.thesis.impads.flink.topology.phases.StaticJoinPhase;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.Serializable;

public final class GiraTravelsTopologyBuilder implements Serializable {

    private static final long serialVersionUID = 1L;

    private final StreamExecutionEnvironment streamExecutionEnvironment;
    private final ConfigurationContainer configurationContainer;
    private final ObjectMapper mapper;
    private final GeometryFactory geoFactory;

    private GiraTravelsTopologyBuilder(final StreamExecutionEnvironment streamExecutionEnvironment
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper
            , final GeometryFactory geoFactory) {
        this.streamExecutionEnvironment = streamExecutionEnvironment;
        this.configurationContainer = configurationContainer;
        this.mapper = mapper;
        this.geoFactory = geoFactory;
    }

    public static final void build(final StreamExecutionEnvironment streamExecutionEnvironment
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper
            , final GeometryFactory geoFactory) {
        GiraTravelsTopologyBuilder builder =
                new GiraTravelsTopologyBuilder(streamExecutionEnvironment
                        , configurationContainer
                        , mapper
                        , geoFactory);

        builder.build();
    }

    private void build() {

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
    }

    private IngestionPhase initializeIngestionPhase() {
        return new IngestionPhase(streamExecutionEnvironment, configurationContainer, mapper);
    }

    private InitialTransformationPhase initializeInitialTransformationPhase(IngestionPhase ingestionPhase) {
        return new InitialTransformationPhase(configurationContainer, ingestionPhase);
    }

    private FirstJoinPhase initializeFirstJoinPhase(InitialTransformationPhase initialTransformationPhase) {
        return new FirstJoinPhase(configurationContainer, initialTransformationPhase);
    }

    private SecondJoinPhase initializeSecondJoinPhase(InitialTransformationPhase initialTransformationPhase
            , FirstJoinPhase firstJoinPhase) {
        return new SecondJoinPhase(configurationContainer, initialTransformationPhase, firstJoinPhase);
    }

    private StaticJoinPhase initializeStaticJoinPhase(SecondJoinPhase secondJoinPhase) {
        return new StaticJoinPhase(configurationContainer, secondJoinPhase);
    }

    private ResultPhase initializeResultPhase(StaticJoinPhase staticJoinPhase) {
        return new ResultPhase(configurationContainer, geoFactory, staticJoinPhase);
    }

    private OutputPhase initializeOutputPhase(ResultPhase resultPhase) {
        return new OutputPhase(configurationContainer, mapper, resultPhase);
    }
}


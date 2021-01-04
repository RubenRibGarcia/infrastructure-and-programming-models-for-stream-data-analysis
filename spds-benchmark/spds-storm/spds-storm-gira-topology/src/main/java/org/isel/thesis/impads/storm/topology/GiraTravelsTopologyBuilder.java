package org.isel.thesis.impads.storm.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.isel.thesis.impads.storm.topology.phases.FirstJoinPhase;
import org.isel.thesis.impads.storm.topology.phases.IngestionPhase;
import org.isel.thesis.impads.storm.topology.phases.ParsePhase;
import org.isel.thesis.impads.storm.topology.phases.OutputPhase;
import org.isel.thesis.impads.storm.topology.phases.Phases;
import org.isel.thesis.impads.storm.topology.phases.ResultPhase;
import org.isel.thesis.impads.storm.topology.phases.SecondJoinPhase;
import org.isel.thesis.impads.storm.topology.phases.StaticJoinPhase;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.Serializable;

public final class GiraTravelsTopologyBuilder implements Serializable {

    private static final long serialVersionUID = 1L;

    private final TopologyBuilder topologyBuilder;
    private final ConfigurationContainer configurationContainer;
    private final ObjectMapper mapper;
    private final GeometryFactory geoFactory;

    private GiraTravelsTopologyBuilder(final TopologyBuilder topologyBuilder
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper
            , final GeometryFactory geoFactory) {
        this.topologyBuilder = topologyBuilder;
        this.configurationContainer = configurationContainer;
        this.mapper = mapper;
        this.geoFactory = geoFactory;
    }

    public static StormTopology build(final TopologyBuilder topologyBuilder
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper
            , final GeometryFactory geoFactory) {
        GiraTravelsTopologyBuilder builder = new GiraTravelsTopologyBuilder(topologyBuilder
                , configurationContainer
                , mapper
                , geoFactory);

        return builder.build();
    }

    private StormTopology build() {

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.INGESTION) {
            initializeIngestionPhase();
        }
        else if (untilPhase == Phases.PARSE) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            initializeParsePhase(ingestionPhase);
        }
        else if (untilPhase == Phases.FIRST_JOIN) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            ParsePhase initialTransformationPhase = initializeParsePhase(ingestionPhase);
            initializeFirstJoinPhase(initialTransformationPhase);
        }
        else if (untilPhase == Phases.SECOND_JOIN){
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            ParsePhase initialTransformationPhase = initializeParsePhase(ingestionPhase);
            FirstJoinPhase firstJoinPhase = initializeFirstJoinPhase(initialTransformationPhase);
            initializeSecondJoinPhase(initialTransformationPhase, firstJoinPhase);
        }
        else if (untilPhase == Phases.STATIC_JOIN) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            ParsePhase initialTransformationPhase = initializeParsePhase(ingestionPhase);
            FirstJoinPhase firstJoinPhase = initializeFirstJoinPhase(initialTransformationPhase);
            SecondJoinPhase secondJoinPhase = initializeSecondJoinPhase(initialTransformationPhase, firstJoinPhase);
            initializeStaticJoinPhase(secondJoinPhase);
        }
        else if (untilPhase == Phases.RESULT) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            ParsePhase initialTransformationPhase = initializeParsePhase(ingestionPhase);
            FirstJoinPhase firstJoinPhase = initializeFirstJoinPhase(initialTransformationPhase);
            SecondJoinPhase secondJoinPhase = initializeSecondJoinPhase(initialTransformationPhase, firstJoinPhase);
            StaticJoinPhase staticJoinPhase = initializeStaticJoinPhase(secondJoinPhase);
            initializeResultPhase(staticJoinPhase);
        }
        else if (untilPhase == Phases.OUTPUT) {
            IngestionPhase ingestionPhase = initializeIngestionPhase();
            ParsePhase initialTransformationPhase = initializeParsePhase(ingestionPhase);
            FirstJoinPhase firstJoinPhase = initializeFirstJoinPhase(initialTransformationPhase);
            SecondJoinPhase secondJoinPhase = initializeSecondJoinPhase(initialTransformationPhase, firstJoinPhase);
            StaticJoinPhase staticJoinPhase = initializeStaticJoinPhase(secondJoinPhase);
            ResultPhase resultPhase = initializeResultPhase(staticJoinPhase);
            initializeOutputPhase(resultPhase);
        }
        else {
            throw new IllegalArgumentException("Unknown " + untilPhase + " phase");
        }

        return topologyBuilder.createTopology();
    }

    private IngestionPhase initializeIngestionPhase() {
        return new IngestionPhase(topologyBuilder, configurationContainer, mapper);
    }

    private ParsePhase initializeParsePhase(IngestionPhase ingestionPhase) {
        return new ParsePhase(topologyBuilder, configurationContainer, ingestionPhase);
    }

    private FirstJoinPhase initializeFirstJoinPhase(ParsePhase initialTransformationPhase) {
        return new FirstJoinPhase(topologyBuilder, configurationContainer, initialTransformationPhase);
    }

    private SecondJoinPhase initializeSecondJoinPhase(ParsePhase initialTransformationPhase
            , FirstJoinPhase firstJoinPhase) {
        return new SecondJoinPhase(topologyBuilder, configurationContainer, initialTransformationPhase, firstJoinPhase);
    }

    private StaticJoinPhase initializeStaticJoinPhase(SecondJoinPhase secondJoinPhase) {
        return new StaticJoinPhase(topologyBuilder, configurationContainer, secondJoinPhase);
    }

    private ResultPhase initializeResultPhase(StaticJoinPhase staticJoinPhase) {
        return new ResultPhase(topologyBuilder, configurationContainer, geoFactory, staticJoinPhase);
    }

    private OutputPhase initializeOutputPhase(ResultPhase resultPhase) {
        return new OutputPhase(topologyBuilder, configurationContainer, mapper, resultPhase);
    }
}

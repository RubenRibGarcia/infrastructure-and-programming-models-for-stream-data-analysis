package org.isel.thesis.impads.storm.topology.phases;

import org.apache.storm.topology.TopologyBuilder;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.topology.ConfigurationContainer;
import org.isel.thesis.impads.storm.topology.bolts.ParserBolt;
import org.isel.thesis.impads.storm.topology.bolts.processor.GiraTravelsWithWazeAndIpmaResultProcessor;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.Serializable;

public class ResultPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String RESULT_STREAM = "result";

    private final TopologyBuilder topologyBuilder;
    private final ConfigurationContainer configurationContainer;
    private final GeometryFactory geoFactory;

    public ResultPhase(final TopologyBuilder topologyBuilder
            , final ConfigurationContainer configurationContainer
            , final GeometryFactory geoFactory
            , final StaticJoinPhase staticJoinPhase) {
        this.topologyBuilder = topologyBuilder;
        this.configurationContainer = configurationContainer;
        this.geoFactory = geoFactory;

        initializePhase(staticJoinPhase);
    }

    private void initializePhase(StaticJoinPhase staticJoinPhase) {
        transformToResult(topologyBuilder, geoFactory, staticJoinPhase.getJoinedGiraTravelsWithWazeAndIpmaStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.RESULT) {
            topologyBuilder.setBolt("observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(RESULT_STREAM);
        }
    }

    private static void transformToResult(TopologyBuilder topologyBuilder
            , GeometryFactory geoFactory
            , String joinedGiraTravelsWithWazeAndIpmaStream) {
        topologyBuilder.setBolt(RESULT_STREAM, ParserBolt.parse(new GiraTravelsWithWazeAndIpmaResultProcessor(geoFactory)))
                .shuffleGrouping(joinedGiraTravelsWithWazeAndIpmaStream);
    }

    public String getResultStream() {
        return RESULT_STREAM;
    }
}

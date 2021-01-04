package org.isel.thesis.impads.storm.topology.phases;

import org.apache.storm.topology.TopologyBuilder;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.topology.ConfigurationContainer;
import org.isel.thesis.impads.storm.topology.bolts.ParserBolt;
import org.isel.thesis.impads.storm.topology.bolts.ResultMapBolt;
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
        transformToResult(configurationContainer
                , topologyBuilder
                , geoFactory
                , staticJoinPhase.getJoinedGiraTravelsWithWazeAndIpmaStream());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.RESULT) {
            topologyBuilder.setBolt("observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(RESULT_STREAM);
        }
    }

    private static void transformToResult(ConfigurationContainer configurationContainer
            , TopologyBuilder topologyBuilder
            , GeometryFactory geoFactory
            , String joinedGiraTravelsWithWazeAndIpmaStream) {
        topologyBuilder.setBolt(RESULT_STREAM
                , new ResultMapBolt(geoFactory)
                , configurationContainer.getTopologyConfiguration().getParallelism())
                .shuffleGrouping(joinedGiraTravelsWithWazeAndIpmaStream);
    }

    public String getResultStream() {
        return RESULT_STREAM;
    }
}

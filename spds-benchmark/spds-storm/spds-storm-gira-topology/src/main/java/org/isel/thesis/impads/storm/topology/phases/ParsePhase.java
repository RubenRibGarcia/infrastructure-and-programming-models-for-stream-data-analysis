package org.isel.thesis.impads.storm.topology.phases;

import org.apache.storm.topology.TopologyBuilder;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.topology.ConfigurationContainer;
import org.isel.thesis.impads.storm.topology.bolts.ParserBolt;
import org.isel.thesis.impads.storm.topology.bolts.processor.GiraTravelsParserProcessor;
import org.isel.thesis.impads.storm.topology.bolts.processor.WazeIrregularitiesParserProcessor;
import org.isel.thesis.impads.storm.topology.bolts.processor.WazeJamsParserProcessor;

import java.io.Serializable;

public class ParsePhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SIMPLIFIED_GIRA_TRAVELS_STREAM = "parse_gira_travels";
    private static final String SIMPLIFIED_WAZE_JAMS_STREAM = "parse_waze_jams";
    private static final String SIMPLIFIED_WAZE_IRREGULARITIES_STREAM = "parse_waze_irregularities";

    private final TopologyBuilder topologyBuilder;
    private final ConfigurationContainer configurationContainer;

    public ParsePhase(final TopologyBuilder topologyBuilder
            , final ConfigurationContainer configurationContainer
            , final IngestionPhase ingestionPhase) {
        this.topologyBuilder = topologyBuilder;
        this.configurationContainer = configurationContainer;

        initializePhase(ingestionPhase);
    }

    private void initializePhase(final IngestionPhase ingestionPhase) {
        parseGiraTravels(ingestionPhase.getGiraTravelsSource());
        parseWazeJams(ingestionPhase.getWazeJamsSource());
        parseWazeIrregularities(ingestionPhase.getWazeIrregularitiesSource());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.PARSE) {
            topologyBuilder.setBolt("simplified_gira_travels_stream_observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(SIMPLIFIED_GIRA_TRAVELS_STREAM);
            topologyBuilder.setBolt("simplified_waze_jams_stream_observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(SIMPLIFIED_WAZE_JAMS_STREAM);
            topologyBuilder.setBolt("simplified_waze_irregularities_stream_observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(SIMPLIFIED_WAZE_IRREGULARITIES_STREAM);
        }
    }

    private void parseGiraTravels(String giraTravelsSource) {
        topologyBuilder.setBolt(SIMPLIFIED_GIRA_TRAVELS_STREAM
                , ParserBolt.parse(new GiraTravelsParserProcessor()), configurationContainer.getTopologyConfiguration().getParallelism())
                .shuffleGrouping(giraTravelsSource);
    }

    private void parseWazeJams(String wazeJamsSource) {
        topologyBuilder.setBolt(SIMPLIFIED_WAZE_JAMS_STREAM
                , ParserBolt.parse(new WazeJamsParserProcessor()), configurationContainer.getTopologyConfiguration().getParallelism())
                .shuffleGrouping(wazeJamsSource);
    }

    private void parseWazeIrregularities(String wazeIrregularitiesSource) {
        topologyBuilder.setBolt(SIMPLIFIED_WAZE_IRREGULARITIES_STREAM
                , ParserBolt.parse(new WazeIrregularitiesParserProcessor()), configurationContainer.getTopologyConfiguration().getParallelism())
                .shuffleGrouping(wazeIrregularitiesSource);
    }

    public String getSimplifiedGiraTravelsStream() {
        return SIMPLIFIED_GIRA_TRAVELS_STREAM;
    }

    public String getSimplifiedWazeJamsStream() {
        return SIMPLIFIED_WAZE_JAMS_STREAM;
    }

    public String getSimplifiedWazeIrregularitiesStream() {
        return SIMPLIFIED_WAZE_IRREGULARITIES_STREAM;
    }




}

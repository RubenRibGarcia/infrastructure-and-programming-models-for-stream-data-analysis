package org.isel.thesis.impads.storm.topology.phases;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.topology.TopologyBuilder;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.spouts.rabbitmq.RMQSpout;
import org.isel.thesis.impads.storm.topology.ConfigurationContainer;
import org.isel.thesis.impads.storm.topology.bolts.ObservableJsonProducer;
import org.isel.thesis.impads.storm.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.storm.topology.models.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.storm.topology.models.WazeJamsSourceModel;

import java.io.Serializable;

public class IngestionPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String GIRA_TRAVELS_SOURCE = "gira_travels_spout";
    private static final String WAZE_JAMS_SOURCE = "waze_jams_spout";
    private static final String WAZE_IRREGULARITIES_SOURCE = "waze_irregularities_spout";

    private final TopologyBuilder topologyBuilder;
    private final ConfigurationContainer configurationContainer;
    private final ObjectMapper mapper;

    public IngestionPhase(final TopologyBuilder topologyBuilder
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper) {
        this.topologyBuilder = topologyBuilder;
        this.configurationContainer = configurationContainer;
        this.mapper = mapper;

        initializePhase();
    }

    private void initializePhase() {
        addGiraTravelsSource();
        addWazeJamsSource();
        addWazeIrregularitiesSource();

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.INGESTION) {
            topologyBuilder.setBolt("ingestion_gira_travels_source_observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(GIRA_TRAVELS_SOURCE);
            topologyBuilder.setBolt("ingestion_waze_jams_source_observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(WAZE_JAMS_SOURCE);
            topologyBuilder.setBolt("ingestion_waze_irregularities_source_observer", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration()))
                    .shuffleGrouping(WAZE_IRREGULARITIES_SOURCE);
        }
    }

    private void addGiraTravelsSource() {
        topologyBuilder.setSpout(GIRA_TRAVELS_SOURCE, RMQSpout.newRabbitMQSpout(configurationContainer.getRabbitMQConfiguration()
                , "gira_travels"
                , true
                , ObservableJsonProducer.observableJsonTupleProducer(mapper
                        , GiraTravelsSourceModel .class
                        , x -> x.getDateStart().toEpochMilli())));
    }

    private void addWazeJamsSource() {
        topologyBuilder.setSpout(WAZE_JAMS_SOURCE, RMQSpout.newRabbitMQSpout(configurationContainer.getRabbitMQConfiguration()
                , "waze_jams"
                , true
                , ObservableJsonProducer.observableJsonTupleProducer(mapper
                        , WazeJamsSourceModel .class
                        , WazeJamsSourceModel::getPubMillis)));
    }

    private void addWazeIrregularitiesSource() {
        topologyBuilder.setSpout(WAZE_IRREGULARITIES_SOURCE, RMQSpout.newRabbitMQSpout(configurationContainer.getRabbitMQConfiguration()
                , "waze_irregularities"
                , true
                , ObservableJsonProducer.observableJsonTupleProducer(mapper
                        , WazeIrregularitiesSourceModel .class
                        , WazeIrregularitiesSourceModel::getDetectionDateMillis)));
    }


    public String getGiraTravelsSource() {
        return GIRA_TRAVELS_SOURCE;
    }

    public String getWazeJamsSource() {
        return WAZE_JAMS_SOURCE;
    }

    public String getWazeIrregularitiesSource() {
        return WAZE_IRREGULARITIES_SOURCE;
    }
}

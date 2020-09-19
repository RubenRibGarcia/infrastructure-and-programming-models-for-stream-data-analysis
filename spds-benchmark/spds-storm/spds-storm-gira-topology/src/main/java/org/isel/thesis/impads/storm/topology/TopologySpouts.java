package org.isel.thesis.impads.storm.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.topology.SpoutDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.isel.thesis.impads.storm.sourcemodel.gira.GiraTravelsSourceModel;
import org.isel.thesis.impads.storm.sourcemodel.waze.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.storm.sourcemodel.waze.WazeJamsSourceModel;
import org.isel.thesis.impads.storm.spouts.rabbitmq.RabbitMQSpout;
import org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.spouts.rabbitmq.func.JsonToTupleProducer;

import static org.isel.thesis.impads.storm.topology.TopologyConstants.*;

public final class TopologySpouts {

    public static void initializeTopologySpouts(RabbitMQConfiguration rabbitMQConfiguration
            , ObjectMapper mapper
            , TopologyBuilder topologyBuilder) {

        final SpoutDeclarer giraTravlesSpoutDeclarer = topologyBuilder.setSpout(Spouts.GIRA_TRAVELS_SPOUT, RabbitMQSpout.newRabbitMQSpout(rabbitMQConfiguration
                , GiraTravelsSourceModel.QUEUE
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , GiraTravelsSourceModel.class
                        , GiraTravelsSourceModel.getTupleField()), Streams.GIRA_TRAVELS_STREAM), 1);

        final SpoutDeclarer wazeJamsSpoutDeclarer = topologyBuilder.setSpout(Spouts.WAZE_JAMS_SPOUT, RabbitMQSpout.newRabbitMQSpout(rabbitMQConfiguration
                , WazeJamsSourceModel.QUEUE
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , WazeJamsSourceModel.class
                        , WazeJamsSourceModel.getTupleField()), Streams.WAZE_JAMS_STREAM), 1);

        final SpoutDeclarer wazeIrregularitiesSpoutDeclarer = topologyBuilder.setSpout(Spouts.WAZE_IRREGULARITIES_SPOUT, RabbitMQSpout.newRabbitMQSpout(rabbitMQConfiguration
                , WazeIrregularitiesSourceModel.QUEUE
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , WazeIrregularitiesSourceModel.class
                        , WazeIrregularitiesSourceModel.getTupleField()), Streams.WAZE_IRREGULARITIES),1);
    }

}

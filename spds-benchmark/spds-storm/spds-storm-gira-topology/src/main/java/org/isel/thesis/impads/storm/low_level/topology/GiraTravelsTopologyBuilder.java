package org.isel.thesis.impads.storm.low_level.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.bolt.JoinBolt;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.isel.thesis.impads.storm.ConfigurationContainer;
import org.isel.thesis.impads.storm.low_level.topology.bolts.GiraTravelsParserProcessor;
import org.isel.thesis.impads.storm.low_level.topology.bolts.ParserBolt;
import org.isel.thesis.impads.storm.low_level.topology.bolts.PrinterBolt;
import org.isel.thesis.impads.storm.low_level.topology.bolts.WazeIrregularitiesParserProcessor;
import org.isel.thesis.impads.storm.low_level.topology.bolts.WazeJamsParserProcessor;
import org.isel.thesis.impads.storm.low_level.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.storm.low_level.topology.models.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.storm.low_level.topology.models.WazeJamsSourceModel;
import org.isel.thesis.impads.storm.spouts.rabbitmq.RMQSpout;
import org.isel.thesis.impads.storm.spouts.rabbitmq.func.JsonToTupleProducer;
import org.locationtech.jts.geom.GeometryFactory;

public final class GiraTravelsTopologyBuilder {


    public static StormTopology build(ConfigurationContainer configurationContainer
            , GeometryFactory geometryFactory
            , ObjectMapper mapper
            , TopologyBuilder builder) {

        builder.setSpout("gira_travels_spout", RMQSpout.newRabbitMQSpout(configurationContainer.getRabbitMQConfiguration()
                , "gira_travels"
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , GiraTravelsSourceModel.class
                        , GiraTravelsSourceModel.getTupleField())));

        builder.setSpout("waze_jams_spout", RMQSpout.newRabbitMQSpout(configurationContainer.getRabbitMQConfiguration()
                , "waze_jams"
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , WazeJamsSourceModel.class
                        , WazeJamsSourceModel.getTupleField())));

        builder.setSpout("waze_irregularities_spout", RMQSpout.newRabbitMQSpout(configurationContainer.getRabbitMQConfiguration()
                , "waze_irregularities"
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , WazeIrregularitiesSourceModel.class
                        , WazeIrregularitiesSourceModel.getTupleField())));

        builder.setBolt("parse_gira_travels"
                , ParserBolt.parse(new GiraTravelsParserProcessor(mapper
                        , GiraTravelsSourceModel.GiraTravelsTupleMapper.mapper())))
                .shuffleGrouping("gira_travels_spout");

        builder.setBolt("parse_waze_jams"
                , ParserBolt.parse(new WazeJamsParserProcessor(mapper
                        , WazeJamsSourceModel.WazeJamsTupleMapper.mapper())))
                .shuffleGrouping("waze_jams_spout");

        builder.setBolt("parse_waze_irregularities"
                , ParserBolt.parse(new WazeIrregularitiesParserProcessor(mapper
                        , WazeIrregularitiesSourceModel.WazeIrregularitiesTupleMapper.mapper())))
                .shuffleGrouping("waze_irregularities_spout");

        builder.setBolt("joined_gira_travels_with_waze_jams", new JoinBolt("parse_gira_travels", "key")
                .join("parse_waze_jams", "key", "parse_gira_travels")
                .select("parse_gira_travels:key,parse_gira_travels:event_timestamp,parse_gira_travels:value,parse_waze_jams:key,parse_waze_jams:value")
                .withWindow(BaseWindowedBolt.Duration.of(5), BaseWindowedBolt.Duration.of(5))
                .withTimestampExtractor(tuple -> tuple.getLongByField("event_timestamp")))
                .fieldsGrouping("parse_gira_travels", new Fields("key"))
                .fieldsGrouping("parse_waze_jams", new Fields("key"));

        builder.setBolt("parse_joined_gira_travels_with_waze_jams")

        builder.setBolt("joined_gira_travels_with_waze", new JoinBolt("joined_gira_travels_with_waze_jams", "parse_gira_travels:key")
                .join("parse_waze_irregularities", "key", "joined_gira_travels_with_waze_jams")
                .select("joined_gira_travels_with_waze_jams:parse_gira_travels:key,parse_waze_irregularites:key")
                .withWindow(BaseWindowedBolt.Duration.of(5), BaseWindowedBolt.Duration.of(5))
                .withTimestampExtractor(tuple -> tuple.getLongByField("key")))
                .fieldsGrouping("")

        builder.setBolt("output", new PrinterBolt())
                .shuffleGrouping("joined_gira_travels_with_waze_jams");

        return builder.createTopology();
    }
}

package org.isel.thesis.impads.storm.topology;

import org.apache.storm.bolt.JoinBolt;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.topology.bolts.ExtendedTopologyBuilder;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.apache.storm.bolt.JoinBolt.Selector.STREAM;
import static org.isel.thesis.impads.storm.topology.TopologyConstants.Bolts;
import static org.isel.thesis.impads.storm.topology.TopologyConstants.Spouts;
import static org.isel.thesis.impads.storm.topology.TopologyConstants.Streams;

public final class GiraTravelsTopologyBuilder implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final double GEOMETRY_BUFFER = 0.0005;

    public static StormTopology build(final ExtendedTopologyBuilder builder
            , final GeometryFactory geoFactory
            , final RabbitMQConfiguration rabbitMQConfiguration) {
        
        builder.setBolt(Bolts.FILTER_AND_MAP_GIRA_TRAVELS, (tuple, collector) -> {
            String geometry = tuple.getStringByField("geometry");
            Integer numVertices = tuple.getIntegerByField("num_vertices");
            Float distance = tuple.getFloatByField("distance");

            if (geometry != null && !geometry.isEmpty()
                    || numVertices != null && numVertices > 1
                    || distance != null && distance > 0) {

                final Instant dateStart = (Instant)tuple.getValueByField("date_start");
                final Long eventTimestamp = dateStart.toEpochMilli();
                final Long keyedTimestamp = dateStart
                        .truncatedTo(ChronoUnit.SECONDS)
                        .toEpochMilli();

                final Long id = tuple.getLongByField("id");

                collector.emit(Streams.GIRA_TRAVELS_STREAM, new Values(keyedTimestamp, eventTimestamp, id, geometry));
            }
        }, Streams.GIRA_TRAVELS_STREAM, new Fields("keyed_timestamp", "event_timestamp", "id", "geometry"), 1)
                .localOrShuffleGrouping(Spouts.GIRA_TRAVELS_SPOUT, Streams.GIRA_TRAVELS_STREAM);

        builder.setBolt(Bolts.FILTER_AND_MAP_WAZE_JAMS, (tuple, collector) -> {
            String geometry = tuple.getStringByField("geometry");

            if (geometry != null && !geometry.isEmpty()) {
                final Long eventTimestamp = tuple.getLongByField("pub_millis");
                final Long keyedTimestamp = Instant.ofEpochMilli(tuple.getLongByField("pub_millis"))
                        .truncatedTo(ChronoUnit.SECONDS)
                        .toEpochMilli();

                final Long id = tuple.getLongByField("id");

                collector.emit(Streams.WAZE_JAMS_STREAM, new Values(keyedTimestamp, eventTimestamp, id, geometry));
            }
        }, Streams.WAZE_JAMS_STREAM, new Fields("keyed_timestamp", "event_timestamp", "id", "geometry"), 1)
                .localOrShuffleGrouping(Spouts.WAZE_JAMS_SPOUT, Streams.WAZE_JAMS_STREAM);

//        builder.setBolt(Bolts.FILTER_AND_MAP_WAZE_IRREGULARITIES, (tuple, collector) -> {
//            String geometry = tuple.getStringByField("geometry");
//
//            if (geometry != null && !geometry.isEmpty()) {
//                final Long eventTimestamp = tuple.getLongByField("detection_date_millis");
//                final Long keyedTimestamp = Instant.ofEpochMilli(tuple.getLongByField("detection_date_millis"))
//                        .truncatedTo(ChronoUnit.HOURS)
//                        .toEpochMilli();
//
//                final Long id = tuple.getLongByField("id");
//
//                collector.emit(Streams.WAZE_IRREGULARITIES, new Values(keyedTimestamp, eventTimestamp, id, geometry));
//            }
//        }, 1, "keyed_timestamp", "event_timestamp", "id", "geometry")
//                .shuffleGrouping(Spouts.WAZE_IRREGULARITIES_SPOUT, Streams.WAZE_IRREGULARITIES);

        JoinBolt joinGiraTravelsWithWazeJamsBolt = new JoinBolt(STREAM, Streams.GIRA_TRAVELS_STREAM, "keyed_timestamp")
                .join(Streams.WAZE_JAMS_STREAM, "keyed_timestamp", Streams.GIRA_TRAVELS_STREAM)
                .select(selectStreamFieldGenerator(Streams.GIRA_TRAVELS_STREAM, "keyed_timestamp"
                        , Streams.WAZE_JAMS_STREAM, "keyed_timestamp"))
                .withTumblingWindow(BaseWindowedBolt.Duration.of(5))
                .withOutputStream(Streams.JOIN_GIRA_TRAVELS_WITH_WAZE_JAMS_STREAM);

        builder.setBolt(Bolts.JOIN_GIRA_TRAVELS_WITH_WAZE_JAMS, joinGiraTravelsWithWazeJamsBolt, 1)
                .partialKeyGrouping(Bolts.FILTER_AND_MAP_GIRA_TRAVELS, Streams.GIRA_TRAVELS_STREAM, new Fields("keyed_timestamp"))
                .partialKeyGrouping(Bolts.FILTER_AND_MAP_WAZE_JAMS, Streams.WAZE_JAMS_STREAM, new Fields("keyed_timestamp"));

        builder.setBolt("printer", new BaseBasicBolt() {
            @Override
            public void execute(Tuple input, BasicOutputCollector collector) {
                System.out.println(input);
            }

            @Override
            public void declareOutputFields(OutputFieldsDeclarer declarer) {

            }
        }).localOrShuffleGrouping(Bolts.JOIN_GIRA_TRAVELS_WITH_WAZE_JAMS, Streams.JOIN_GIRA_TRAVELS_WITH_WAZE_JAMS_STREAM);

//        builder.setBolt("filter_gira_with_waze_jams_geom", SimpleBoltsAPI.filterBolt(tuple -> {
//            try {
//                WKBReader reader = new WKBReader(geoFactory);
//                final Geometry giraGeo
//                        = reader.read(WKBReader.hexToBytes(tuple.getStringByField("gira_travels_geometry")));
//                final Geometry wazeJamGeo
//                        = reader.read(WKBReader.hexToBytes(tuple.getStringByField("waze_jams_geometry")));
//
//                final Geometry giraTravelStartingPoint =
//                        ((LineString) giraGeo.getGeometryN(0))
//                                .getStartPoint()
//                                .buffer(GEOMETRY_BUFFER);
//
//                return giraTravelStartingPoint.intersects(wazeJamGeo);
//            }
//            catch (Exception e) {
//                throw new RuntimeException(e.getMessage(), e);
//            }
//        }, new Fields("gira_travels_keyed_timestamp", "gira_travels_event_timestamp", "gira_travels_id", "gira_travels_geometry"
//                , "waze_jams_keyed_timestamp", "waze_jams_event_timestamp", "waze_jams_id", "waze_jams_geometry")))
//                .shuffleGrouping("join_gira_travels_with_waze_jams");
//
//        JoinBolt joinJoinedGiraJamsWithWazeIrregularities = new JoinBolt("filter_gira_with_waze_jams_geom", "gira_travels_keyed_timestamp")
//                .join("mapped_ts_waze_irregularities", "waze_irregularities_keyed_timestamp", "filter_gira_with_waze_jams_geom")
//                .select("gira_travels_keyed_timestamp,gira_travels_event_timestamp,gira_travels_id,gira_travels_geometry" +
//                        "waze_jams_keyed_timestamp,waze_jams_event_timestamp,waze_jams_id,waze_jams_geometry" +
//                        "waze_irregularities_keyed_timestamp,waze_irregularities_event_timestamp,waze_irregularities_id,waze_irregularities_geometry")
//                .withTumblingWindow(BaseWindowedBolt.Duration.seconds(5));
//
//        builder.setBolt("join_joined_gira_jams_with_irregularities", joinJoinedGiraJamsWithWazeIrregularities)
//                .fieldsGrouping("filter_gira_with_waze_jams_geom", new Fields("gira_travels_keyed_timestamp"))
//                .fieldsGrouping("mapped_ts_waze_irregularities", new Fields("waze_irregularities_keyed_timestamp"));
//
//        builder.setBolt("filter_joined_gira_jams_with_irregularities", SimpleBoltsAPI.filterBolt(tuple -> {
//            try {
//                WKBReader reader = new WKBReader(geoFactory);
//                final Geometry giraGeo
//                        = reader.read(WKBReader.hexToBytes(tuple.getStringByField("gira_travels_geometry")));
//                final Geometry wazeIrrGeo
//                        = reader.read(WKBReader.hexToBytes(tuple.getStringByField("waze_irregularities_geometry")));
//
//                final Geometry giraTravelStartingPoint =
//                        ((LineString) giraGeo.getGeometryN(0))
//                                .getStartPoint()
//                                .buffer(GEOMETRY_BUFFER);
//
//                return giraTravelStartingPoint.intersects(wazeIrrGeo);
//            }
//            catch (Exception e) {
//                throw new RuntimeException(e.getMessage(), e);
//            }
//        }, new Fields("gira_travels_keyed_timestamp", "gira_travels_event_timestamp", "gira_travels_id", "gira_travels_geometry"
//                , "waze_jams_keyed_timestamp", "waze_jams_event_timestamp", "waze_jams_id", "waze_jams_geometry"
//                , "waze_irregularities_keyed_timestamp", "waze_irregularities_event_timestamp", "waze_irregularities_id", "waze_irregularities_geometry")))
//                .shuffleGrouping("join_joined_gira_jams_with_irregularities");
//
//        builder.setBolt("gira_travels_with_waze_result", SimpleBoltsAPI.mapBolt(tuple -> {
//            try {
//                boolean jamAndIrrMatches = false;
//
//                WKBReader reader = new WKBReader(geoFactory);
//                final Geometry wazeIrrGeo
//                        = reader.read(WKBReader.hexToBytes(tuple.getStringByField("waze_irregularities_geometry")));
//                final Geometry wazeJamGeo
//                        = reader.read(WKBReader.hexToBytes(tuple.getStringByField("waze_jams_geometry")));
//
//                if (wazeIrrGeo.equalsExact(wazeJamGeo, 0.0005)) {
//                    jamAndIrrMatches = true;
//                }
//
//                return new Values(tuple.getLongByField("gira_travels_id")
//                        , tuple.getStringByField("gira_travels_geometry")
//                        , tuple.getLongByField("waze_jams_id")
//                        , tuple.getStringByField("waze_jams_geometry")
//                        , tuple.getLongByField("waze_irregularities_id")
//                        , tuple.getStringByField("waze_irregularities_geometry")
//                        , jamAndIrrMatches
//                        , Instant.now().toEpochMilli());
//            }
//            catch (Exception e) {
//                throw new RuntimeException(e.getMessage(), e);
//            }
//        }, GiraTravelsWithWazeResult.getTupleField()))
//            .shuffleGrouping("filter_joined_gira_jams_with_irregularities");
//
//        builder.setBolt("storm_output", RabbitMQBolt.newRabbitMQBolt(rabbitMQConfiguration
//                , IRabbitMQQueue.RabbitMQQueueNaming.withName("storm_output")))
//            .shuffleGrouping("gira_travels_with_waze_result");


        return builder.createTopology();
    }

    private static String selectStreamFieldGenerator(String... keyValuePair) {
        String selectField = "";
        int index = 0;
        while (index < keyValuePair.length) {
            selectField += keyValuePair[index];
            if (index % 2 == 0) {
                selectField += ":";
            }
            else {
                selectField += ",";
            }
            index++;
        }

        return selectField;
    }
}

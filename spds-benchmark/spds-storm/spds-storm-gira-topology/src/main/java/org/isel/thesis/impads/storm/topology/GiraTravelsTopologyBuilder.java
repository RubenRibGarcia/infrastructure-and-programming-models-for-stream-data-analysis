package org.isel.thesis.impads.storm.topology;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.topology.bolts.ObservableJsonProducer;
import org.isel.thesis.impads.storm.topology.bolts.ParserBolt;
import org.isel.thesis.impads.storm.topology.bolts.join.KeySelector;
import org.isel.thesis.impads.storm.topology.bolts.join.ObservableJoinBolt;
import org.isel.thesis.impads.storm.topology.bolts.join.TupleFieldSelector;
import org.isel.thesis.impads.storm.topology.bolts.processor.GiraTravelsParserProcessor;
import org.isel.thesis.impads.storm.topology.bolts.processor.GiraTravelsWithWazeAndIpmaResultProcessor;
import org.isel.thesis.impads.storm.topology.bolts.processor.WazeIrregularitiesParserProcessor;
import org.isel.thesis.impads.storm.topology.bolts.processor.WazeJamsParserProcessor;
import org.isel.thesis.impads.storm.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.storm.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.storm.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.storm.topology.models.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.storm.topology.models.WazeJamsSourceModel;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.redis.bolt.RedisBoltBuilder;
import org.isel.thesis.impads.storm.redis.bolt.RedisMapperBolt;
import org.isel.thesis.impads.storm.redis.bolt.RedisStoreBolt;
import org.isel.thesis.impads.storm.spouts.rabbitmq.RMQSpout;
import org.isel.thesis.impads.storm.topology.utils.IpmaUtils;
import org.isel.thesis.impads.structures.Tuple2;
import org.isel.thesis.impads.structures.Tuple3;
import org.isel.thesis.impads.structures.Tuple4;
import org.locationtech.jts.geom.GeometryFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class GiraTravelsTopologyBuilder {


    public static StormTopology build(ConfigurationContainer configurationContainer
            , GeometryFactory geometryFactory
            , ObjectMapper mapper
            , TopologyBuilder builder) {

        builder.setSpout("gira_travels_spout", RMQSpout.newRabbitMQSpout(configurationContainer.getRabbitMQConfiguration()
                , "gira_travels"
                , true
                , ObservableJsonProducer.observableJsonTupleProducer(mapper
                        , GiraTravelsSourceModel.class
                        , x -> x.getDateStart().toEpochMilli())));

        builder.setSpout("waze_jams_spout", RMQSpout.newRabbitMQSpout(configurationContainer.getRabbitMQConfiguration()
                , "waze_jams"
                , true
                , ObservableJsonProducer.observableJsonTupleProducer(mapper
                        , WazeJamsSourceModel.class
                        , WazeJamsSourceModel::getPubMillis)));

        builder.setSpout("waze_irregularities_spout", RMQSpout.newRabbitMQSpout(configurationContainer.getRabbitMQConfiguration()
                , "waze_irregularities"
                , true
                , ObservableJsonProducer.observableJsonTupleProducer(mapper
                        , WazeIrregularitiesSourceModel.class
                        , WazeIrregularitiesSourceModel::getDetectionDateMillis)));

        builder.setBolt("parse_gira_travels"
                , ParserBolt.parse(new GiraTravelsParserProcessor()))
                .shuffleGrouping("gira_travels_spout");

        builder.setBolt("parse_waze_jams"
                , ParserBolt.parse(new WazeJamsParserProcessor()))
                .shuffleGrouping("waze_jams_spout");

        builder.setBolt("parse_waze_irregularities"
                , ParserBolt.parse(new WazeIrregularitiesParserProcessor()))
                .shuffleGrouping("waze_irregularities_spout");

        ObservableJoinBolt<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>
                , Long> joinedGiraTravelsWithWazeJamsBolt = ObservableJoinBolt.JoinBuilder
                .<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>, Long>from("parse_gira_travels"
                        , KeySelector.selector(fn -> Instant.ofEpochMilli(fn.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        , TupleFieldSelector.selector(tuple -> (Observable<SimplifiedGiraTravelsModel>) tuple.getValueByField("value")))
                .join("parse_waze_jams"
                        , KeySelector.selector(fn -> Instant.ofEpochMilli(fn.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        , TupleFieldSelector.selector(tuple -> (Observable<SimplifiedWazeJamsModel>) tuple.getValueByField("value")))
                .apply((from, join) -> {
                    Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> obs =
                            from.join(Tuple2.of(from.getData(), join.getData()), join);

                    Values values = new Values(Instant.ofEpochMilli(obs.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                            , obs.getEventTimestamp()
                            , obs);

                    return values;
                })
                .outputFields("key", "event_timestamp", "value")
                .build();

        builder.setBolt("joined_gira_travels_with_waze_jams", joinedGiraTravelsWithWazeJamsBolt
                .withWindow(BaseWindowedBolt.Duration.of(5), BaseWindowedBolt.Duration.of(5))
                .withTimestampExtractor(tuple -> tuple.getLongByField("event_timestamp")))
                .fieldsGrouping("parse_gira_travels", new Fields("key"))
                .fieldsGrouping("parse_waze_jams", new Fields("key"));

        ObservableJoinBolt<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>, Observable<SimplifiedWazeIrregularitiesModel>
                , Long> joinedGiraTravelsWithWazeBolt = ObservableJoinBolt.JoinBuilder
                .<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>, Observable<SimplifiedWazeIrregularitiesModel>, Long>from("joined_gira_travels_with_waze_jams"
                        , KeySelector.selector(fn -> Instant.ofEpochMilli(fn.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        , TupleFieldSelector.selector(tuple -> (Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>) tuple.getValueByField("value")))
                .join("parse_waze_irregularities"
                        , KeySelector.selector(fn -> Instant.ofEpochMilli(fn.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        , TupleFieldSelector.selector(tuple -> (Observable<SimplifiedWazeIrregularitiesModel>) tuple.getValueByField("value")))
                .apply((from, join) -> {
                    Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> obs =
                            from.join(Tuple3.of(from.getData().getFirst(), from.getData().getSecond(), join.getData()), join);

                    Values values = new Values(Instant.ofEpochMilli(obs.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                            , obs.getEventTimestamp()
                            , obs);

                    return values;
                })
                .outputFields("key", "event_timestamp", "value")
                .build();

        builder.setBolt("joined_gira_travels_with_waze", joinedGiraTravelsWithWazeBolt
                .withWindow(BaseWindowedBolt.Duration.of(30), BaseWindowedBolt.Duration.of(30))
                .withTimestampExtractor(tuple -> tuple.getLongByField("event_timestamp")))
                .fieldsGrouping("joined_gira_travels_with_waze_jams", new Fields("key"))
                .fieldsGrouping("parse_waze_irregularities", new Fields("key"));

        RedisMapperBolt<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> joinedGiraTravelsWithWazeAndIpma =
                RedisBoltBuilder.<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>>mapper(configurationContainer.getRedisConfiguration())
                .tupleMapper(t -> (Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>) t.getValueByField("value"))
                .transform((redisCommandsContainer, t) -> {
                    String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(t.getData().getFirst().getEventTimestamp()));
                    IpmaValuesModel ipmaValues = IpmaValuesModel.fetchAndAddFromRedis(hashField, redisCommandsContainer);

                    Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel> rvalue =
                            Tuple4.of(t.getData().getFirst(), t.getData().getSecond(), t.getData().getThird(), ipmaValues);
                    return new Values(t.map(rvalue));
                })
                .outputFields("value")
                .build();

        builder.setBolt("joined_gira_travels_with_waze_and_ipma", joinedGiraTravelsWithWazeAndIpma)
                .shuffleGrouping("joined_gira_travels_with_waze");

        builder.setBolt("result", ParserBolt.parse(new GiraTravelsWithWazeAndIpmaResultProcessor(geometryFactory)))
                .shuffleGrouping("joined_gira_travels_with_waze_and_ipma");

        RedisStoreBolt<Observable<GiraTravelsWithWazeAndIpmaResult>> resultBolt =
                RedisBoltBuilder.<Observable<GiraTravelsWithWazeAndIpmaResult>>store(configurationContainer.getRedisConfiguration())
                .tupleMapper(t -> (Observable<GiraTravelsWithWazeAndIpmaResult>) t.getValueByField("value"))
                .consume((container, t) -> {
                    try {
                        container.rpush("storm_output", mapper.writeValueAsString(t));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .build();

        builder.setBolt("output", ObservableBolt.observe(configurationContainer.getMetricsCollectorConfiguration(), resultBolt))
                .shuffleGrouping("result");

        return builder.createTopology();
    }
}

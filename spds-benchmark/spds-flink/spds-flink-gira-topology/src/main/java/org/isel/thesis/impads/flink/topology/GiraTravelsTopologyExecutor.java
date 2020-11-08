package org.isel.thesis.impads.flink.topology;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.redis.RedisProcessFunction;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.mapper.json.RPushJsonMapper;
import org.apache.flink.util.Collector;
import org.isel.thesis.impads.flink.metrics.ObservableFilterFunction;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.flink.topology.models.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.flink.topology.models.WazeJamsSourceModel;
import org.isel.thesis.impads.flink.topology.utils.IpmaUtils;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.WKBReader;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class GiraTravelsTopologyExecutor implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final double GEOMETRY_BUFFER = 0.0005;

    public static void execute(final ConfigurationContainer config
            , final ObjectMapper mapper
            , final StreamExecutionEnvironment env
            , final TopologySources sources
            , final GeometryFactory geoFactory) {

        DataStream<Observable<SimplifiedGiraTravelsModel>> giraTravelsDataStream =
                sources.getGiraTravelsSource()
                        .filter(ObservableFilterFunction.observe(config.getMetricsCollectorConfiguration(), model ->
                                model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()
                                || model.getData().getNumberOfVertices() != null && model.getData().getNumberOfVertices() > 1
                                || model.getData().getDistance() != null && model.getData().getDistance() > 0))
                        .map(new MapFunction<Observable<GiraTravelsSourceModel>
                                , Observable<SimplifiedGiraTravelsModel>>() {
                            @Override
                            public Observable<SimplifiedGiraTravelsModel> map(
                                    Observable<GiraTravelsSourceModel> model) throws Exception {

                                return ObservableImpl.map(model, new SimplifiedGiraTravelsModel(String.valueOf(model.getData().getId())
                                        , model.getData().getGeometry()
                                        , model.getEventTimestamp()));
                            }
                        });

        DataStream<Observable<SimplifiedWazeJamsModel>> wazeJamsDataStream =
                sources.getWazeJamsSource()
                        .filter(ObservableFilterFunction.observe(config.getMetricsCollectorConfiguration()
                                , model -> model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()))
                        .map(new MapFunction<Observable<WazeJamsSourceModel>
                                , Observable<SimplifiedWazeJamsModel>>() {
                            @Override
                            public Observable<SimplifiedWazeJamsModel> map(
                                    Observable<WazeJamsSourceModel> model) throws Exception {

                                return ObservableImpl.map(model, new SimplifiedWazeJamsModel(String.valueOf(model.getData().getId())
                                        , model.getData().getGeometry()
                                        , model.getEventTimestamp()));
                            }
                        });

        DataStream<Observable<SimplifiedWazeIrregularitiesModel>> wazeIrregularitiesDataStream =
                sources.getWazeIrregularitiesSource()
                        .filter(ObservableFilterFunction.observe(config.getMetricsCollectorConfiguration()
                                , model -> model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()))
                        .map(new MapFunction<Observable<WazeIrregularitiesSourceModel>
                                , Observable<SimplifiedWazeIrregularitiesModel>>() {
                            @Override
                            public Observable<SimplifiedWazeIrregularitiesModel> map(
                                    Observable<WazeIrregularitiesSourceModel> model) throws Exception {

                                return ObservableImpl.map(model, new SimplifiedWazeIrregularitiesModel(String.valueOf(model.getData().getId())
                                        , model.getData().getGeometry()
                                        , model.getEventTimestamp()));
                            }
                        });

        DataStream<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> joinedGiraTravelsWithWazeJams =
                giraTravelsDataStream.keyBy(k -> Instant.ofEpochMilli(k.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        .intervalJoin(wazeJamsDataStream.keyBy(k -> Instant.ofEpochMilli(k.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()))
                        .between(Time.milliseconds(-5), Time.milliseconds(5))
                        .process(new ProcessJoinFunction<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>, Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>>() {
                            @Override
                            public void processElement(Observable<SimplifiedGiraTravelsModel> left
                                    , Observable<SimplifiedWazeJamsModel> right
                                    , Context context
                                    , Collector<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> collector) throws Exception {

                                final Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel> tuple =
                                        Tuple2.of(left.getData(), right.getData());

                                collector.collect(ObservableImpl.join(tuple, left, right));
                            }
                        });

        DataStream<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> joinedGiraTravelsWithWaze =
                joinedGiraTravelsWithWazeJams.keyBy(k -> Instant.ofEpochMilli(k.getData().f0.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        .intervalJoin(wazeIrregularitiesDataStream.keyBy(k -> Instant.ofEpochMilli(k.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()))
                        .between(Time.milliseconds(-5), Time.milliseconds(5))
                        .process(new ProcessJoinFunction<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>, Observable<SimplifiedWazeIrregularitiesModel>, Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>>() {
                            @Override
                            public void processElement(Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> left
                                    , Observable<SimplifiedWazeIrregularitiesModel> right
                                    , Context context
                                    , Collector<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> collector) throws Exception {

                                final Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel> tuple =
                                        Tuple3.of(left.getData().f0, left.getData().f1, right.getData());

                                collector.collect(ObservableImpl.join(tuple, left, right));
                            }
                        });

        DataStream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> joinedGiraTravelsWithWazeAndIpma =
                joinedGiraTravelsWithWaze
                        .process(new RedisProcessFunction<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>, Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>>(config.getRedisConfiguration()) {
                            @Override
                            public void processElement(Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> tuple
                                    , Context context
                                    , Collector<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> collector)
                                    throws Exception {
                                String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(tuple.getData().f0.getEventTimestamp()));
                                IpmaValuesModel rvalue = IpmaValuesModel.fetchAndAddFromRedis(hashField, redisCommandsContainer);

                                collector.collect(ObservableImpl.map(tuple
                                        , Tuple4.of(tuple.getData().f0, tuple.getData().f1, tuple.getData().f2, rvalue)));
                            }
                        });

        DataStream<Observable<GiraTravelsWithWazeAndIpmaResult>> result =
                joinedGiraTravelsWithWazeAndIpma.map(new MapFunction<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>, Observable<GiraTravelsWithWazeAndIpmaResult>>() {
                    @Override
                    public Observable<GiraTravelsWithWazeAndIpmaResult> map(Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> tuple) throws Exception {
                        boolean jamAndIrrMatches = false;

                        WKBReader reader = new WKBReader(geoFactory);
                        final Geometry giraGeo
                                = reader.read(WKBReader.hexToBytes(tuple.getData().f0.getGeometry()));
                        final Geometry wazeIrrGeo
                                = reader.read(WKBReader.hexToBytes(tuple.getData().f2.getGeometry()));
                        final Geometry wazeJamGeo
                                = reader.read(WKBReader.hexToBytes(tuple.getData().f1.getGeometry()));

                        final Geometry giraTravelStartingPoint =
                                ((LineString) giraGeo.getGeometryN(0))
                                        .getStartPoint()
                                        .buffer(GEOMETRY_BUFFER);

                        if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
                            jamAndIrrMatches = true;
                        }

                        GiraTravelsWithWazeAndIpmaResult rvalue = new GiraTravelsWithWazeAndIpmaResult(tuple.getData().f0
                                , tuple.getData().f1
                                , tuple.getData().f2
                                , tuple.getData().f3
                                , giraTravelStartingPoint.intersects(wazeJamGeo)
                                , giraTravelStartingPoint.intersects(wazeIrrGeo)
                                , jamAndIrrMatches);

                        return ObservableImpl.map(tuple, rvalue);
                    }
                });

        result.addSink(ObservableSinkFunction.observe(config.getMetricsCollectorConfiguration()
                , new RedisSink<>(config.getRedisConfiguration()
                        , new RPushJsonMapper<>(mapper, "flink_output"))));
    }
}


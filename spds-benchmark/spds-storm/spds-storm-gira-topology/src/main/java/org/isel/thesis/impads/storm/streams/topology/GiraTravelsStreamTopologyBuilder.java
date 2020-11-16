package org.isel.thesis.impads.storm.streams.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.streams.Pair;
import org.apache.storm.streams.PairStream;
import org.apache.storm.streams.RedisStream;
import org.apache.storm.streams.Stream;
import org.apache.storm.streams.StreamBuilder;
import org.apache.storm.streams.operations.ValueJoiner;
import org.apache.storm.streams.windowing.SlidingWindows;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.geotools.geometry.jts.WKBReader;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.metrics.ObservableBolt;
import org.isel.thesis.impads.storm.redis.bolt.RedisStoreBolt;
import org.isel.thesis.impads.storm.redis.common.mapper.json.RPushJsonMapper;
import org.isel.thesis.impads.storm.streams.data.structures.Tuple2;
import org.isel.thesis.impads.storm.streams.data.structures.Tuple3;
import org.isel.thesis.impads.storm.streams.data.structures.Tuple4;
import org.isel.thesis.impads.storm.streams.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.storm.streams.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.storm.streams.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.storm.streams.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.storm.streams.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.storm.streams.topology.utils.IpmaUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class GiraTravelsStreamTopologyBuilder {

    private static final double GEOMETRY_BUFFER = 0.0005;

    public static StormTopology build(final StreamBuilder builder
            , final TopologyStreamSources topologySources
            , final GeometryFactory geoFactory
            , final ConfigurationContainer config
            , final ObjectMapper mapper) {

        PairStream<Long, Observable<SimplifiedGiraTravelsModel>> pairStreamGiraTravels = topologySources.getGiraTravelsSourceModelStream()
                .filter(model -> model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()
                        || model.getData().getNumberOfVertices() != null && model.getData().getNumberOfVertices() > 1
                        || model.getData().getDistance() != null && model.getData().getDistance() > 0)
                .mapToPair(model ->
                        Pair.of(Instant.ofEpochMilli(model.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                                , model.map(new SimplifiedGiraTravelsModel(String.valueOf(model.getData().getId())
                                        , model.getData().getGeometry()
                                        , model.getEventTimestamp()))));

        PairStream<Long, Observable<SimplifiedWazeJamsModel>> pairStreamWazeJams = topologySources.getWazeJamsSourceModelStream()
                .filter(model ->
                        model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty())
                .mapToPair(model ->
                        Pair.of(Instant.ofEpochMilli(model.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                                , model.map(new SimplifiedWazeJamsModel(String.valueOf(model.getData().getId())
                                        , model.getData().getGeometry()
                                        , model.getEventTimestamp()))));

        PairStream<Long, Observable<SimplifiedWazeIrregularitiesModel>> pairStreamWazeIrregularities = topologySources.getWazeIrregularitiesSourceModelStream()
                .filter(model ->
                        model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty())
                .mapToPair(model -> Pair.of(Instant.ofEpochMilli(model.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                        , model.map(new SimplifiedWazeIrregularitiesModel(String.valueOf(model.getData().getId())
                                , model.getData().getGeometry()
                                , model.getEventTimestamp()))));

        PairStream<Long, Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> joinedGiraTravelsWithWazeJams =
                pairStreamGiraTravels.window(SlidingWindows.of(BaseWindowedBolt.Duration.of(5), BaseWindowedBolt.Duration.of(5))
                        .withLag(BaseWindowedBolt.Duration.of(5)))
                .join(pairStreamWazeJams
                        , (ValueJoiner<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>, Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>>)
                                (value1, value2) -> {
                                    Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel> pair
                                            = Tuple2.of(value1.getData(), value2.getData());

                                    return value1.join(pair, value2);
                                });

        PairStream<Long, Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> joinedGiraTravelsWithWaze =
                joinedGiraTravelsWithWazeJams.window(SlidingWindows.of(BaseWindowedBolt.Duration.of(5), BaseWindowedBolt.Duration.of(5))
                        .withLag(BaseWindowedBolt.Duration.of(5)))
                .join(pairStreamWazeIrregularities
                        , (ValueJoiner<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>, Observable<SimplifiedWazeIrregularitiesModel>, Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>>)
                                (value1, value2) -> {
                                    Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel> tuple3
                                            = Tuple3.of(value1.getData().getFirst(), value1.getData().getSecond(), value2.getData());

                                    return value1.join(tuple3, value2);
                                });

        Stream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> joinedGiraTravelsWithWazeAndIpma =
                RedisStream.map(joinedGiraTravelsWithWaze, config.getRedisConfiguration(), (redis, input) -> {
                    Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> model =
                            input.getSecond();

                    String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(model.getData().getFirst().getEventTimestamp()));
                    IpmaValuesModel rvalue = IpmaValuesModel.fetchAndAddFromRedis(hashField, redis);

                    return model.map(Tuple4.of(model.getData().getFirst(), model.getData().getSecond(), model.getData().getThird(), rvalue));
                });

        Stream<Observable<GiraTravelsWithWazeAndIpmaResult>> result = joinedGiraTravelsWithWazeAndIpma
                .map(model -> {
                    try {
                        boolean jamAndIrrMatches = false;

                        WKBReader reader = new WKBReader(geoFactory);
                        final Geometry giraGeo
                                = reader.read(WKBReader.hexToBytes(model.getData().getFirst().getGeometry()));
                        final Geometry wazeIrrGeo
                                = reader.read(WKBReader.hexToBytes(model.getData().getThird().getGeometry()));
                        final Geometry wazeJamGeo
                                = reader.read(WKBReader.hexToBytes(model.getData().getSecond().getGeometry()));

                        final Geometry giraTravelStartingPoint =
                                ((LineString) giraGeo.getGeometryN(0))
                                        .getStartPoint()
                                        .buffer(GEOMETRY_BUFFER);

                        if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
                            jamAndIrrMatches = true;
                        }

                        GiraTravelsWithWazeAndIpmaResult rvalue = new GiraTravelsWithWazeAndIpmaResult(model.getData().getFirst()
                                , model.getData().getSecond()
                                , model.getData().getThird()
                                , model.getData().getFourth()
                                , giraTravelStartingPoint.intersects(wazeJamGeo)
                                , giraTravelStartingPoint.intersects(wazeIrrGeo)
                                , jamAndIrrMatches);

                        return model.map(rvalue);
                    }
                    catch(Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                });

        result.to(ObservableBolt.observe(config.getMetricsCollectorConfiguration()
                , new RedisStoreBolt(config.getRedisConfiguration(), new RPushJsonMapper(mapper, "storm_output"))));

        return builder.build();
    }
}

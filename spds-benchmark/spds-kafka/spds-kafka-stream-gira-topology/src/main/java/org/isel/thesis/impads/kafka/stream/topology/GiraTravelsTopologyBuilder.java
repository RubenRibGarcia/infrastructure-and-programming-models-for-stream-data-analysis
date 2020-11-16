package org.isel.thesis.impads.kafka.stream.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.geotools.geometry.jts.WKBReader;
import org.isel.thesis.impads.kafka.stream.connectors.redis.common.container.RedisCommandsContainer;
import org.isel.thesis.impads.kafka.stream.data.structures.Tuple2;
import org.isel.thesis.impads.kafka.stream.data.structures.Tuple3;
import org.isel.thesis.impads.kafka.stream.data.structures.Tuple4;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.kafka.stream.topology.model.IpmaValuesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableGiraTravelsWithWazeResults;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWaze;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeAndIpma;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeJams;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.utils.IpmaUtils;
import org.isel.thesis.impads.kafka.stream.topology.utils.SerdesUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class GiraTravelsTopologyBuilder {

    private static final Logger logger = LoggerFactory.getLogger(GiraTravelsTopologyBuilder.class);

    private static final double GEOMETRY_BUFFER = 0.0005;

    public static Topology build(final StreamsBuilder streamsBuilder
            , final TopologySources topologySources
            , final GeometryFactory geoFactory
            , final ObjectMapper mapper
            , final KafkaStreamObservableMetricsCollector observableMetricsCollector
            , final RedisCommandsContainer redisContainer) {

        KStream<Long, ObservableSimplifiedGiraTravelsModel> observableGiraTravelsStream = topologySources.getGiraTravelsStream()
                .filter((k, v) ->
                        (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty())
                                || v.getData().getNumberOfVertices() != null && v.getData().getNumberOfVertices() > 1
                                || v.getData().getDistance() != null && v.getData().getDistance() > 0)
                .map((k, v) -> KeyValue.pair(Instant.ofEpochMilli(v.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli(), new ObservableSimplifiedGiraTravelsModel(v.map(new SimplifiedGiraTravelsModel(String.valueOf(v.getData().getId())
                            , v.getData().getGeometry()
                            , v.getEventTimestamp())))));

        KStream<Long, ObservableSimplifiedWazeJamsModel> observableWazeJamsStream = topologySources.getWazeJamsStream()
                .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty()))
                .map((k, v) -> KeyValue.pair(Instant.ofEpochMilli(v.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli(), new ObservableSimplifiedWazeJamsModel(v.map(new SimplifiedWazeJamsModel(String.valueOf(v.getData().getId())
                           , v.getData().getGeometry()
                           , v.getEventTimestamp())))));

        KStream<Long, ObservableSimplifiedWazeIrregularitiesModel> observableWazeIrregularitiesStream = topologySources.getWazeIrregularitiesStream()
                .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty()))
                .map((k, v) -> KeyValue.pair(Instant.ofEpochMilli(v.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli(), new ObservableSimplifiedWazeIrregularitiesModel(v.map(new SimplifiedWazeIrregularitiesModel(String.valueOf(v.getData().getId())
                        , v.getData().getGeometry()
                        , v.getEventTimestamp())))));


        KStream<Long, ObservableJoinedGiraTravelsWithWazeJams> joinedGiraTravelsWithWazeJams =
                observableGiraTravelsStream
                        .join(observableWazeJamsStream
                                , (left, right) -> new ObservableJoinedGiraTravelsWithWazeJams(left.join(Tuple2.of(left.getData(), right.getData()), right))
                                , JoinWindows.of(Duration.ofMillis(5))
                                , StreamJoined.with(Serdes.Long()
                                        , SerdesUtils.simplifiedGiraTravelsSerdes(mapper)
                                        , SerdesUtils.simplifiedWazeJamsSerdes(mapper)));

        KStream<Long, ObservableJoinedGiraTravelsWithWaze> joinedGiraTravelsWithWaze =
                joinedGiraTravelsWithWazeJams
                        .join(observableWazeIrregularitiesStream
                                , (left, right) -> new ObservableJoinedGiraTravelsWithWaze(left.join(Tuple3.of(left.getData().getFirst(), left.getData().getSecond(), right.getData()), right))
                                , JoinWindows.of(Duration.ofMillis(5))
                                , StreamJoined.with(Serdes.Long()
                                                , SerdesUtils.joinedGiraTravelsWithWazeJamsJsonSerdes(mapper)
                                                , SerdesUtils.simplifiedWazeIrregularitiesSerdes(mapper)));

        KStream<Long, ObservableJoinedGiraTravelsWithWazeAndIpma> joinedGiraTravelsWithWazeAndIpma =
                joinedGiraTravelsWithWaze.mapValues(v -> {
                    String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(v.getData().getFirst().getEventTimestamp()));
                    IpmaValuesModel rvalue = IpmaValuesModel.fetchAndAddFromRedis(hashField, redisContainer);

                    return new ObservableJoinedGiraTravelsWithWazeAndIpma(
                            v.map(Tuple4.of(v.getData().getFirst(), v.getData().getSecond(), v.getData().getThird(), rvalue)));
                });


        KStream<Long, ObservableGiraTravelsWithWazeResults> result =
                joinedGiraTravelsWithWazeAndIpma
                        .map((k, v) -> {
                            try {
                                boolean jamAndIrrMatches = false;

                                WKBReader reader = new WKBReader(geoFactory);
                                final Geometry giraGeo
                                        = reader.read(WKBReader.hexToBytes(v.getData().getFirst().getGeometry()));
                                final Geometry wazeIrrGeo
                                        = reader.read(WKBReader.hexToBytes(v.getData().getThird().getGeometry()));
                                final Geometry wazeJamGeo
                                        = reader.read(WKBReader.hexToBytes(v.getData().getSecond().getGeometry()));

                                final Geometry giraTravelStartingPoint =
                                        ((LineString) giraGeo.getGeometryN(0))
                                                .getStartPoint()
                                                .buffer(GEOMETRY_BUFFER);

                                if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
                                    jamAndIrrMatches = true;
                                }

                                GiraTravelsWithWazeAndIpmaResult rvalue = new GiraTravelsWithWazeAndIpmaResult(v.getData().getFirst()
                                        , v.getData().getSecond()
                                        , v.getData().getThird()
                                        , v.getData().getFourth()
                                        , giraTravelStartingPoint.intersects(wazeJamGeo)
                                        , giraTravelStartingPoint.intersects(wazeIrrGeo)
                                        , jamAndIrrMatches);

                                return KeyValue.pair(k, new ObservableGiraTravelsWithWazeResults(v.map(rvalue)));
                            }
                            catch(Exception e) {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        })
                .peek((k,v) -> observableMetricsCollector.collect(v));


        result.to("kafka_result", Produced.with(Serdes.Long(), SerdesUtils.giraTravelsWithWazeResultsJsonSerdes(mapper)));

        return streamsBuilder.build();
    }
}

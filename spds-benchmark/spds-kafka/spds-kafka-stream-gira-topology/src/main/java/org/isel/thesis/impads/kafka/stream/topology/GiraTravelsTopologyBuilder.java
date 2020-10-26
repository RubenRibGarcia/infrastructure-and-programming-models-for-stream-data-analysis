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
import org.isel.thesis.impads.kafka.stream.data.structures.Tuple2;
import org.isel.thesis.impads.kafka.stream.data.structures.Tuple3;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsWithWazeResult;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableGiraTravelsWithWazeResults;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWaze;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeJams;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.utils.ObservableMeasure;
import org.isel.thesis.impads.kafka.stream.topology.utils.SerdesUtils;
import org.isel.thesis.impads.metrics.ObservableImpl;
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
            , final ObservableMeasure observableMeasure) {

        KStream<Long, ObservableSimplifiedGiraTravelsModel> observableGiraTravelsStream = topologySources.getGiraTravelsStream()
                .filter((k, v) ->
                        (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty())
                                || v.getData().getNumberOfVertices() != null && v.getData().getNumberOfVertices() > 1
                                || v.getData().getDistance() != null && v.getData().getDistance() > 0)
                .map((k, v) -> KeyValue.pair(Instant.ofEpochMilli(v.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli(), new ObservableSimplifiedGiraTravelsModel(ObservableImpl.map(v, new SimplifiedGiraTravelsModel(String.valueOf(v.getData().getId())
                            , v.getData().getGeometry()
                            , v.getEventTimestamp())))));

        KStream<Long, ObservableSimplifiedWazeJamsModel> observableWazeJamsStream = topologySources.getWazeJamsStream()
                .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty()))
                .map((k, v) -> KeyValue.pair(Instant.ofEpochMilli(v.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli(), new ObservableSimplifiedWazeJamsModel(ObservableImpl.map(v, new SimplifiedWazeJamsModel(String.valueOf(v.getData().getId())
                           , v.getData().getGeometry()
                           , v.getEventTimestamp())))));

        KStream<Long, ObservableSimplifiedWazeIrregularitiesModel> observableWazeIrregularitiesStream = topologySources.getWazeIrregularitiesStream()
                .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty()))
                .map((k, v) -> KeyValue.pair(Instant.ofEpochMilli(v.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli(), new ObservableSimplifiedWazeIrregularitiesModel(ObservableImpl.map(v, new SimplifiedWazeIrregularitiesModel(String.valueOf(v.getData().getId())
                        , v.getData().getGeometry()
                        , v.getEventTimestamp())))));


        KStream<Long, ObservableJoinedGiraTravelsWithWazeJams> joinedGiraTravelsWithWazeJams =
                observableGiraTravelsStream
                        .join(observableWazeJamsStream
                                , (left, right) -> new ObservableJoinedGiraTravelsWithWazeJams(ObservableImpl.join(Tuple2.of(left.getData(), right.getData()), left, right))
                                , JoinWindows.of(Duration.ofMillis(5))
                                , StreamJoined.with(Serdes.Long()
                                        , SerdesUtils.simplifiedGiraTravelsSerdes(mapper)
                                        , SerdesUtils.simplifiedWazeJamsSerdes(mapper)));

        KStream<Long, ObservableJoinedGiraTravelsWithWaze> joinedGiraTravelsWithWaze =
                joinedGiraTravelsWithWazeJams
                        .join(observableWazeIrregularitiesStream
                                , (left, right) -> new ObservableJoinedGiraTravelsWithWaze(ObservableImpl.join(Tuple3.of(left.getData().getFirst(), left.getData().getSecond(), right.getData()), left, right))
                                , JoinWindows.of(Duration.ofMillis(5))
                                , StreamJoined.with(Serdes.Long()
                                                , SerdesUtils.joinedGiraTravelsWithWazeJamsJsonSerdes(mapper)
                                                , SerdesUtils.simplifiedWazeIrregularitiesSerdes(mapper)));

        KStream<Long, ObservableGiraTravelsWithWazeResults> result =
                joinedGiraTravelsWithWaze
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

                                GiraTravelsWithWazeResult rvalue = new GiraTravelsWithWazeResult(v.getData().getFirst()
                                        , v.getData().getSecond()
                                        , v.getData().getThird()
                                        , giraTravelStartingPoint.intersects(wazeJamGeo)
                                        , giraTravelStartingPoint.intersects(wazeIrrGeo)
                                        , jamAndIrrMatches);

                                return KeyValue.pair(k, new ObservableGiraTravelsWithWazeResults(ObservableImpl.map(v, rvalue)));
                            }
                            catch(Exception e) {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        })
                .peek((k,v) -> observableMeasure.measure(v));


        result.to("kafka_result", Produced.with(Serdes.Long(), SerdesUtils.giraTravelsWithWazeResultsJsonSerdes(mapper)));

        return streamsBuilder.build();
    }
}

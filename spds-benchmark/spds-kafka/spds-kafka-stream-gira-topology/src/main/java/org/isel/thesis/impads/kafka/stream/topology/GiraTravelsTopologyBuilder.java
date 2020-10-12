package org.isel.thesis.impads.kafka.stream.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.geotools.geometry.jts.WKBReader;
import org.isel.thesis.impads.kafka.stream.data.structures.Tuple2;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsWithWazeResult;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.utils.ObservableMeasure;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.metrics.api.Observable;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.time.Duration;

public final class GiraTravelsTopologyBuilder {

    private static final double GEOMETRY_BUFFER = 0.0005;

    public static Topology build(final StreamsBuilder streamsBuilder
            , final TopologySources topologySources
            , final GeometryFactory geoFactory
            , final ObjectMapper mapper
            , final ObservableMeasure observableMeasure) {

        KStream<Void, Observable<SimplifiedGiraTravelsModel>> giraTravelsKStream =
                topologySources.getGiraTravelsStream()
                        .filter((k, v) ->
                                (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty())
                                        || v.getData().getNumberOfVertices() != null && v.getData().getNumberOfVertices() > 1
                                        || v.getData().getDistance() != null && v.getData().getDistance() > 0)
                        .map((k, v) -> KeyValue.pair(null, ObservableImpl.map(v, new SimplifiedGiraTravelsModel(String.valueOf(v.getData().getId())
                                    , v.getData().getGeometry()
                                    , v.getEventTimestamp()))));

        KStream<Void, Observable<SimplifiedWazeJamsModel>> wazeJamsKStream =
                topologySources.getWazeJamsStream()
                        .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty()))
                        .map((k, v) -> KeyValue.pair(null, ObservableImpl.map(v, new SimplifiedWazeJamsModel(String.valueOf(v.getData().getId())
                                   , v.getData().getGeometry()
                                   , v.getEventTimestamp()))));

        KStream<Void, Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> joinedGiraTravelsWithWazeJams =
                giraTravelsKStream.join(wazeJamsKStream
                        , (left, right) -> ObservableImpl.join(Tuple2.of(left.getData(), right.getData()), left, right)
                        , JoinWindows.of(Duration.ofMillis(5)));

        KStream<Void, Observable<GiraTravelsWithWazeResult>> joinedGiraTravelsWithWaze =
                joinedGiraTravelsWithWazeJams.map((k, v) -> {
                    try {
                        boolean jamAndIrrMatches = false;

                        WKBReader reader = new WKBReader(geoFactory);
                        final Geometry giraGeo
                                = reader.read(WKBReader.hexToBytes(v.getData().getFirst().getGeometry()));
//                    final Geometry wazeIrrGeo
//                            = reader.read(WKBReader.hexToBytes(tuple2Observable.getData().f1.getGeometry()));
                        final Geometry wazeJamGeo
                                = reader.read(WKBReader.hexToBytes(v.getData().getSecond().getGeometry()));

                        final Geometry giraTravelStartingPoint =
                                ((LineString) giraGeo.getGeometryN(0))
                                        .getStartPoint()
                                        .buffer(GEOMETRY_BUFFER);

//                        if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
//                            jamAndIrrMatches = true;
//                        }

                        GiraTravelsWithWazeResult rvalue = new GiraTravelsWithWazeResult(v.getData().getFirst()
                                , v.getData().getSecond()
                                , null
                                , giraTravelStartingPoint.intersects(wazeJamGeo)
//                                , giraTravelStartingPoint.intersects(wazeIrrGeo)
                                , false
                                , jamAndIrrMatches);

                        return KeyValue.pair(null, ObservableImpl.map(v, rvalue));
                    }
                    catch(Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                });

        joinedGiraTravelsWithWaze
                .peek((k, v) -> observableMeasure.measure(v));

        joinedGiraTravelsWithWaze.to("kafka_result");

        return streamsBuilder.build();
    }
}

package org.isel.thesis.impads.storm.streams.topology;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.streams.Pair;
import org.apache.storm.streams.PairStream;
import org.apache.storm.streams.Stream;
import org.apache.storm.streams.StreamBuilder;
import org.apache.storm.streams.windowing.SlidingWindows;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.geotools.geometry.jts.WKBReader;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.streams.topology.models.GiraTravelsWithWazeResult;
import org.isel.thesis.impads.storm.streams.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.storm.streams.topology.models.SimplifiedWazeJamsModel;
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
            , final RabbitMQConfiguration rabbitMQConfiguration) {

        PairStream<Long, Observable<SimplifiedGiraTravelsModel>> pairStreamGiraTravels = topologySources.getGiraTravelsSourceModelStream()
                .filter(model -> (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty())
                        || model.getData().getNumberOfVertices() != null && model.getData().getNumberOfVertices() > 1
                        || model.getData().getDistance() != null && model.getData().getDistance() > 0)
                .map(model -> ObservableImpl.map(model, new SimplifiedGiraTravelsModel(String.valueOf(model.getData().getId())
                            , model.getData().getGeometry()
                            , model.getEventTimestamp())))
                .mapToPair( model ->
                        Pair.of(Instant.ofEpochMilli(model.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                                , model));

        PairStream<Long, Observable<SimplifiedWazeJamsModel>> pairStreamWazeJams = topologySources.getWazeJamsSourceModelStream()
                .filter(model -> (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()))
                .map(model -> ObservableImpl.map(model, new SimplifiedWazeJamsModel(String.valueOf(model.getData().getId())
                        , model.getData().getGeometry()
                        , model.getEventTimestamp())))
                .mapToPair(model -> Pair.of(Instant.ofEpochMilli(model.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()
                        , model));

        PairStream<Long, Pair<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>>> joinedGiraTravelsWithWazJams =
                pairStreamGiraTravels.window(SlidingWindows.of(BaseWindowedBolt.Duration.of(5), BaseWindowedBolt.Duration.of(5))
                        .withLag(BaseWindowedBolt.Duration.of(5)))
                .join(pairStreamWazeJams);

        Stream<GiraTravelsWithWazeResult> result = joinedGiraTravelsWithWazJams
                .map(pair -> {
                    try {
                        boolean jamAndIrrMatches = false;

                        WKBReader reader = new WKBReader(geoFactory);
                        final Geometry giraGeo
                                = reader.read(WKBReader.hexToBytes(pair.getSecond().getFirst().getData().getGeometry()));
                        final Geometry wazeJamGeo
                                = reader.read(WKBReader.hexToBytes(pair.getSecond().getSecond().getData().getGeometry()));

                        final Geometry giraTravelStartingPoint =
                                ((LineString) giraGeo.getGeometryN(0))
                                        .getStartPoint()
                                        .buffer(GEOMETRY_BUFFER);

//                        if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
//                            jamAndIrrMatches = true;
//                        }

                        GiraTravelsWithWazeResult rvalue = new GiraTravelsWithWazeResult(pair.getSecond().getFirst().getData()
                                , pair.getSecond().getSecond().getData()
                                , null
                                , giraTravelStartingPoint.intersects(wazeJamGeo)
//                                , giraTravelStartingPoint.intersects(wazeIrrGeo)
                                , false
                                , jamAndIrrMatches);

                        return rvalue;
                    }
                    catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return builder.build();
    }
}

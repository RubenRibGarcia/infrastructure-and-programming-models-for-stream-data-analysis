package org.isel.thesis.impads.kafka.stream.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsSourceModel;
import org.locationtech.jts.geom.GeometryFactory;

public final class GiraTravelsTopologyBuilder {

    private static final double GEOMETRY_BUFFER = 0.0005;

    public static Topology build(final StreamsBuilder streamsBuilder
            , final TopologySources topologySources
            , final GeometryFactory geoFactory
            , final ObjectMapper mapper) {

        streamsBuilder.<Void, String>stream(GiraTravelsSourceModel.KAFKA_TOPIC)
            .peek((k, v) -> System.out.println(v));
//        topologySources.getGiraTravelsStream()
//                .peek((k, v) -> System.out.println("Gira id: " + v.getData().getId()));

//        topologySources.getGiraTravelsStream()
//                .peek((k,v) -> System.out.println("Gira id: " + v.getData().getId()))
//                .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty())
//                        || v.getData().getNumberOfVertices() != null && v.getData().getNumberOfVertices() > 1
//                        || v.getData().getDistance() != null && v.getData().getDistance() > 0)
//                .map((k, v) -> {
//                    long keyedTimestamp = Instant
//                            .ofEpochMilli(v.getEventTime())
//                            .truncatedTo(ChronoUnit.HOURS).toEpochMilli();
//
//                    return KeyValue.pair(keyedTimestamp
//                            , MeasureWrapper.convert(FactoryDataModel.from(v.getData()), v));
//                }).to("simplified_gira_travels_model", Produced.with(Serdes.Long(), MeasureWrapperSerdes.newMeasureWrapperSerdes(mapper, SimplifiedGiraTravelsModel.class)));
//
//        topologySources.getWazeJamsStream()
//                .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty()))
//                .map((k, v) -> {
//                    long keyedTimestamp = Instant
//                            .ofEpochMilli(v.getEventTime())
//                            .truncatedTo(ChronoUnit.HOURS).toEpochMilli();
//
//                    return KeyValue.pair(keyedTimestamp
//                            , MeasureWrapper.convert(FactoryDataModel.from(v.getData()), v));
//                }).to("simplified_waze_jams_model", Produced.with(Serdes.Long(), MeasureWrapperSerdes.newMeasureWrapperSerdes(mapper, SimplifiedWazeJamsModel.class)));
//
//        topologySources.getWazeIrregularitiesStream()
//                .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty()))
//                .map((k, v) -> {
//                    long keyedTimestamp = Instant
//                            .ofEpochMilli(v.getEventTime())
//                            .truncatedTo(ChronoUnit.HOURS).toEpochMilli();
//
//                    return KeyValue.pair(keyedTimestamp
//                            , MeasureWrapper.convert(FactoryDataModel.from(v.getData()), v));
//                }).to("simplified_waze_irregularities_model", Produced.with(Serdes.Long(), MeasureWrapperSerdes.newMeasureWrapperSerdes(mapper, SimplifiedWazeIrregularitiesModel.class)));
//
//        KStream<Long, Tuple2<IMeasureWrapper<SimplifiedGiraTravelsModel>, IMeasureWrapper<SimplifiedWazeJamsModel>>> joinedGiraWithWazeJams =
//                streamsBuilder.stream("simplified_gira_travels_model", Consumed.with(Serdes.Long(), MeasureWrapperSerdes.newMeasureWrapperSerdes(mapper, SimplifiedGiraTravelsModel.class)))
//                        .join(streamsBuilder.stream("simplified_waze_jams_model", Consumed.with(Serdes.Long(), MeasureWrapperSerdes.newMeasureWrapperSerdes(mapper, SimplifiedWazeJamsModel.class)))
//                                , Tuple2::of
//                                , JoinWindows.of(Duration.ofMillis(5)));
//
//        KStream<Long, Tuple3<IMeasureWrapper<SimplifiedGiraTravelsModel>, IMeasureWrapper<SimplifiedWazeJamsModel>, IMeasureWrapper<SimplifiedWazeIrregularitiesModel>>> joinedGiraWithWazeStream =
//                joinedGiraWithWazeJams
//                        .join(streamsBuilder.stream("simplified_waze_irregularities_model", Consumed.with(Serdes.Long(), MeasureWrapperSerdes.newMeasureWrapperSerdes(mapper, SimplifiedWazeIrregularitiesModel.class)))
//                                , new ValueJoiner<Tuple2<IMeasureWrapper<SimplifiedGiraTravelsModel>, IMeasureWrapper<SimplifiedWazeJamsModel>>, IMeasureWrapper<SimplifiedWazeIrregularitiesModel>, Tuple3<IMeasureWrapper<SimplifiedGiraTravelsModel>, IMeasureWrapper<SimplifiedWazeJamsModel>, IMeasureWrapper<SimplifiedWazeIrregularitiesModel>>>() {
//                                    @Override
//                                    public Tuple3<IMeasureWrapper<SimplifiedGiraTravelsModel>, IMeasureWrapper<SimplifiedWazeJamsModel>, IMeasureWrapper<SimplifiedWazeIrregularitiesModel>> apply(
//                                            Tuple2<IMeasureWrapper<SimplifiedGiraTravelsModel>, IMeasureWrapper<SimplifiedWazeJamsModel>> joinedTuple
//                                            , IMeasureWrapper<SimplifiedWazeIrregularitiesModel> tuple) {
//
//                                        return Tuple3.of(joinedTuple.getFirst(), joinedTuple.getSecond(), tuple);
//                                    }
//                                }
//                                , JoinWindows.of(Duration.ofMillis(5)));
//
//        KStream<Void, GiraTravelsWithWazeResult> resultStream =
//                joinedGiraWithWazeStream
//                        .map((k, v) -> {
//                            try {
//                                boolean jamAndIrrMatches = false;
//
//                                WKBReader reader = new WKBReader(geoFactory);
//                                final Geometry giraGeo
//                                        = reader.read(WKBReader.hexToBytes(v.getFirst().getData().getGeometry()));
//                                final Geometry wazeJamGeo
//                                        = reader.read(WKBReader.hexToBytes(v.getSecond().getData().getGeometry()));
//                                final Geometry wazeIrrGeo
//                                        = reader.read(WKBReader.hexToBytes(v.getThird().getData().getGeometry()));
//
//                                final Geometry giraTravelStartingPoint =
//                                        ((LineString) giraGeo.getGeometryN(0))
//                                                .getStartPoint()
//                                                .buffer(GEOMETRY_BUFFER);
//
//                                if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
//                                    jamAndIrrMatches = true;
//                                }
//
//                                long nowTimestamp = Instant.now().toEpochMilli();
//                                v.getFirst().setProcessingTime(nowTimestamp);
//                                v.getSecond().setProcessingTime(nowTimestamp);
//                                v.getThird().setProcessingTime(nowTimestamp);
//
//                                return KeyValue.pair(null, new GiraTravelsWithWazeResult(v.getFirst()
//                                        , v.getSecond()
//                                        , v.getThird()
//                                        , giraTravelStartingPoint.intersects(wazeJamGeo)
//                                        , giraTravelStartingPoint.intersects(wazeIrrGeo)
//                                        , jamAndIrrMatches));
//                            } catch (Exception e) {
//                                throw new RuntimeException(e.getMessage(), e);
//                            }
//                        });
//
//        resultStream.to("kafka_result"
//                , Produced.with(Serdes.Void(), JsonSerdes.newJsonSerders(mapper, GiraTravelsWithWazeResult.class)));

        return streamsBuilder.build();
    }
}

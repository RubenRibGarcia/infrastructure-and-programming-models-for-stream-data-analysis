package org.isel.thesis.impads.storm.streams.topology;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.streams.StreamBuilder;
import org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration;
import org.locationtech.jts.geom.GeometryFactory;

public final class GiraTravelsStreamTopologyBuilder {

    private static final double GEOMETRY_BUFFER = 0.0005;

    public static StormTopology build(final StreamBuilder builder
            , final TopologyStreamSources topologySources
            , final GeometryFactory geoFactory
            , final RabbitMQConfiguration rabbitMQConfiguration) {
//
//        PairStream<Long, Observable<SimplifiedGiraTravelsModel>> giraTravelStream =
//                topologySources.getGiraTravelsSourceModelStream()
//                .filter(model -> (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty())
//                        || model.getData().getNumberOfVertices() != null && model.getData().getNumberOfVertices() > 1
//                        || model.getData().getDistance() != null && model.getData().getDistance() > 0)
//                .mapToPair(input -> {
//                    long keyedTimestamp = Instant
//                            .ofEpochMilli(input.getEventTimestamp())
//                            .truncatedTo(ChronoUnit.HOURS).toEpochMilli();
//
//                    return Pair.of(keyedTimestamp, ObservableImpl.convert(FactoryDataModel.from(input.getData()), input));
//                });
//
//        PairStream<Long, Observable<SimplifiedWazeIrregularitiesModel>> wazeIrregularitiesStream =
//                topologySources.getWazeIrregularitiesSourceModelStream()
//                .filter(model -> (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()))
//                .mapToPair(input -> {
//                    long keyedTimestamp = Instant
//                            .ofEpochMilli(input.getEventTime())
//                            .truncatedTo(ChronoUnit.HOURS).toEpochMilli();
//
//                    return Pair.of(keyedTimestamp, ObservableImpl.convert(FactoryDataModel.from(input.getData()), input));
//                });
//
//        PairStream<Long, Observable<SimplifiedWazeJamsModel>> wazeJamsStream = topologySources.getWazeJamsSourceModelStream()
//                .filter(model -> (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()))
//                .mapToPair(input -> {
//                    long keyedTimestamp = Instant
//                            .ofEpochMilli(input.getEventTime())
//                            .truncatedTo(ChronoUnit.HOURS).toEpochMilli();
//
//                    return Pair.of(keyedTimestamp, ObservableImpl.convert(FactoryDataModel.from(input.getData()), input));
//                });
//
//        PairStream<Long, Pair<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>>> joinedGiraWithWazeJamsStream =
//                giraTravelStream
//                        .window(TumblingWindows.of(BaseWindowedBolt.Duration.of(5)))
//                        .join(wazeJamsStream);
//
//        PairStream<Long, Pair<Pair<Observable<SimplifiedGiraTravelsModel>, Observable<SimplifiedWazeJamsModel>>
//                , Observable<SimplifiedWazeIrregularitiesModel>>> resultStream =
//                joinedGiraWithWazeJamsStream
//                        .window(TumblingWindows.of(BaseWindowedBolt.Duration.of(5)))
//                        .join(wazeIrregularitiesStream);
//
//        resultStream.to(RabbitMQBolt.newRabbitMQBolt(rabbitMQConfiguration
//                            , IRabbitMQQueue.RabbitMQQueueNaming.withName("storm_output")));

        return builder.build();
    }
}

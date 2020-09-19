package org.isel.thesis.impads.flink.topology;

import com.typesafe.config.Config;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.geotools.geometry.jts.WKBReader;
import org.isel.thesis.impads.flink.rabbitmq.connector.DataStreamRMQSink;
import org.isel.thesis.impads.flink.rabbitmq.connector.serdes.JsonSerializationSchema;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.IRMQQueue;
import org.isel.thesis.impads.flink.topology.datamodel.GiraTravelsWithWazeResult;
import org.isel.thesis.impads.flink.topology.datamodel.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.datamodel.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.datamodel.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.flink.topology.sourcemodel.gira.GiraTravelsSourceModel;
import org.isel.thesis.impads.flink.topology.sourcemodel.waze.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.flink.topology.sourcemodel.waze.WazeJamsSourceModel;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class GiraTravelsTopologyExecutor implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final double GEOMETRY_BUFFER = 0.0005;

    public static void execute(final Config config
            , final ObjectMapper mapper
            , final StreamExecutionEnvironment env
            , final TopologySources sources
            , final GeometryFactory geoFactory) {

        DataStream<Observable<SimplifiedGiraTravelsModel>> giraTravelsDataStream =
                sources.getGiraTravelsSource()
                        .filter(model -> (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty())
                                || model.getData().getNumberOfVertices() != null && model.getData().getNumberOfVertices() > 1
                                || model.getData().getDistance() != null && model.getData().getDistance() > 0)
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
                        .filter(model -> (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()))
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
                        .filter(model -> (model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()))
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
                            public void processElement(Observable<SimplifiedGiraTravelsModel> simplifiedGiraTravelsModelObservable
                                    , Observable<SimplifiedWazeJamsModel> simplifiedWazeJamsModelObservable
                                    , Context context
                                    , Collector<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>> collector) throws Exception {

                                final Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel> tuple =
                                        Tuple2.of(simplifiedGiraTravelsModelObservable.getData(), simplifiedWazeJamsModelObservable.getData());

                                collector.collect(ObservableImpl.join(tuple, simplifiedGiraTravelsModelObservable, simplifiedWazeJamsModelObservable));
                            }
                        });

        DataStream<Observable<GiraTravelsWithWazeResult>> result =
                joinedGiraTravelsWithWazeJams.keyBy(k -> Instant.ofEpochMilli(k.getData().f0.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
                        .intervalJoin(wazeIrregularitiesDataStream.keyBy(k -> Instant.ofEpochMilli(k.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli()))
                        .between(Time.milliseconds(-5), Time.milliseconds(5))
                        .process(new ProcessJoinFunction<Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>>, Observable<SimplifiedWazeIrregularitiesModel>, Observable<GiraTravelsWithWazeResult>>() {

                            @Override
                            public void processElement(Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> tuple2Observable
                                    , Observable<SimplifiedWazeIrregularitiesModel> simplifiedWazeIrregularitiesModelObservable
                                    , Context context
                                    , Collector<Observable<GiraTravelsWithWazeResult>> collector) throws Exception {
                                boolean jamAndIrrMatches = false;

                                WKBReader reader = new WKBReader(geoFactory);
                                final Geometry giraGeo
                                        = reader.read(WKBReader.hexToBytes(tuple2Observable.getData().f0.getGeometry()));
                                final Geometry wazeIrrGeo
                                        = reader.read(WKBReader.hexToBytes(tuple2Observable.getData().f1.getGeometry()));
                                final Geometry wazeJamGeo
                                        = reader.read(WKBReader.hexToBytes(simplifiedWazeIrregularitiesModelObservable.getData().getGeometry()));

                                final Geometry giraTravelStartingPoint =
                                        ((LineString) giraGeo.getGeometryN(0))
                                                .getStartPoint()
                                                .buffer(GEOMETRY_BUFFER);

                                if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
                                    jamAndIrrMatches = true;
                                }

                                GiraTravelsWithWazeResult rvalue = new GiraTravelsWithWazeResult(tuple2Observable.getData().f0
                                        , tuple2Observable.getData().f1
                                        , simplifiedWazeIrregularitiesModelObservable.getData()
                                        , giraTravelStartingPoint.intersects(wazeJamGeo)
                                        , giraTravelStartingPoint.intersects(wazeIrrGeo)
                                        , jamAndIrrMatches);

                                collector.collect(ObservableImpl.join(rvalue, tuple2Observable, simplifiedWazeIrregularitiesModelObservable));
                            }
                        });

        result.addSink(ObservableSinkFunction.observe(config, DataStreamRMQSink.newRabbitMQSink(config
                        , IRMQQueue.RMQQueueNaming.withName("flink_output")
                        , JsonSerializationSchema.newJsonSerializationSchema(mapper))));
    }
}

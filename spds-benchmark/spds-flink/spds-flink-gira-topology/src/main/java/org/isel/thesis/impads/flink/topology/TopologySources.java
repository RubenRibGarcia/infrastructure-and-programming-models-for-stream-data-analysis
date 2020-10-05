package org.isel.thesis.impads.flink.topology;

import com.typesafe.config.Config;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.isel.thesis.impads.flink.rabbitmq.connector.DataStreamRMQSource;
import org.isel.thesis.impads.flink.rabbitmq.connector.serdes.JsonDeserializationSchema;
import org.isel.thesis.impads.flink.metrics.ObservableSourceDeserializer;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.flink.topology.models.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.flink.topology.models.WazeJamsSourceModel;
import org.isel.thesis.impads.metrics.api.Observable;

import java.io.Serializable;
import java.time.Duration;

public class TopologySources implements Serializable {

    private static final long serialVersionUID = 1L;

    private final DataStream<Observable<GiraTravelsSourceModel>> giraTravelsSource;
    private final DataStream<Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesSource;
    private final DataStream<Observable<WazeJamsSourceModel>> wazeJamsSource;

    private TopologySources(final DataStream<Observable<GiraTravelsSourceModel>> giraTravelsSource
            , final DataStream<Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesSource
            , final DataStream<Observable<WazeJamsSourceModel>> wazeJamsSource) {

        this.giraTravelsSource = giraTravelsSource;
        this.wazeIrregularitiesSource = wazeIrregularitiesSource;
        this.wazeJamsSource = wazeJamsSource;
    }

    public static TopologySources initializeTopologySources(final StreamExecutionEnvironment env
            , final Config conf
            , final ObjectMapper mapper) {

        return new TopologySources(initializeGiraTravelsSource(env, conf, mapper)
                , initializeWazeIrregularitiesSource(env, conf, mapper)
                , initializeWazeJamsSource(env, conf, mapper));
    }

    private static DataStream<Observable<GiraTravelsSourceModel>> initializeGiraTravelsSource(
            final StreamExecutionEnvironment env
            , final Config conf
            , final ObjectMapper mapper) {

        DeserializationSchema<Observable<GiraTravelsSourceModel>> deserializationSchema = ObservableSourceDeserializer
                .appendObservable(JsonDeserializationSchema.newJsonDeserializationSchema(mapper, TypeInformation.of(GiraTravelsSourceModel.class))
                        , x -> x.getDateStart().toEpochMilli());

        return env.addSource(DataStreamRMQSource
                .newRabbitMQSource(conf
                        , GiraTravelsSourceModel.QUEUE
                        , deserializationSchema))
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Observable<GiraTravelsSourceModel>>forBoundedOutOfOrderness(Duration.ofMillis(5))
                        .withTimestampAssigner((event, timestamp) -> event.getData().getDateStart().toEpochMilli()))
                .name("GiraTravelsSource");
    }

    private static DataStream<Observable<WazeIrregularitiesSourceModel>> initializeWazeIrregularitiesSource(
            final StreamExecutionEnvironment env
            , final Config conf
            , final ObjectMapper mapper) {

        DeserializationSchema<Observable<WazeIrregularitiesSourceModel>> deserializationSchema = ObservableSourceDeserializer
                .appendObservable(JsonDeserializationSchema.newJsonDeserializationSchema(mapper, TypeInformation.of(WazeIrregularitiesSourceModel.class))
                        , WazeIrregularitiesSourceModel::getDetectionDateMillis);

        return env.addSource(DataStreamRMQSource.newRabbitMQSource(conf
                , WazeIrregularitiesSourceModel.QUEUE
                , deserializationSchema))
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Observable<WazeIrregularitiesSourceModel>>forBoundedOutOfOrderness(Duration.ofMillis(5))
                        .withTimestampAssigner((event, timestamp) -> event.getData().getDetectionDateMillis()))
                .name("WazeIrregularitiesSource");
    }

    private static DataStream<Observable<WazeJamsSourceModel>> initializeWazeJamsSource(
            final StreamExecutionEnvironment env
            , final Config conf
            , final ObjectMapper mapper) {

        DeserializationSchema<Observable<WazeJamsSourceModel>> deserializationSchema = ObservableSourceDeserializer
                .appendObservable(JsonDeserializationSchema.newJsonDeserializationSchema(mapper, TypeInformation.of(WazeJamsSourceModel.class))
                        , WazeJamsSourceModel::getPubMillis);

        return env.addSource(DataStreamRMQSource.newRabbitMQSource(conf
                , WazeJamsSourceModel.QUEUE
                , deserializationSchema))
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Observable<WazeJamsSourceModel>>forBoundedOutOfOrderness(Duration.ofMillis(5))
                        .withTimestampAssigner((event, timestamp) -> event.getData().getPubMillis()))
                .name("WazeJamsSource");
    }

    public DataStream<Observable<GiraTravelsSourceModel>> getGiraTravelsSource() {
        return giraTravelsSource;
    }

    public DataStream<Observable<WazeIrregularitiesSourceModel>> getWazeIrregularitiesSource() {
        return wazeIrregularitiesSource;
    }

    public DataStream<Observable<WazeJamsSourceModel>> getWazeJamsSource() {
        return wazeJamsSource;
    }
}

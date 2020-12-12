package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.metrics.ObservableSourceDeserializer;
import org.isel.thesis.impads.flink.rabbitmq.connector.DataStreamRMQSource;
import org.isel.thesis.impads.flink.rabbitmq.connector.serdes.JsonDeserializationSchema;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.flink.topology.models.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.flink.topology.models.WazeJamsSourceModel;
import org.isel.thesis.impads.metrics.Observable;

import java.io.Serializable;
import java.time.Duration;

public class IngestionPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final StreamExecutionEnvironment streamExecutionEnvironment;
    private final ConfigurationContainer configurationContainer;
    private final ObjectMapper mapper;

    private DataStream<Observable<GiraTravelsSourceModel>> giraTravelsSource;
    private DataStream<Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesSource;
    private DataStream<Observable<WazeJamsSourceModel>> wazeJamsSource;

    public IngestionPhase(final StreamExecutionEnvironment streamExecutionEnvironment
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper) {
        this.streamExecutionEnvironment = streamExecutionEnvironment;
        this.configurationContainer = configurationContainer;
        this.mapper = mapper;

        initializePhase();
    }

    private void initializePhase() {
        this.giraTravelsSource = addGiraTravelsSource();
        this.wazeJamsSource = addWazeJamsSource();
        this.wazeIrregularitiesSource = addWazeIrregularitiesSource();

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.INGESTION) {
            giraTravelsSource.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
            wazeJamsSource.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
            wazeIrregularitiesSource.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
        }
    }

    private DataStream<Observable<GiraTravelsSourceModel>> addGiraTravelsSource() {

        DeserializationSchema<Observable<GiraTravelsSourceModel>> deserializationSchema = ObservableSourceDeserializer
                .appendObservable(JsonDeserializationSchema.newJsonDeserializationSchema(mapper, TypeInformation.of(GiraTravelsSourceModel.class))
                        , x -> x.getDateStart().toEpochMilli());

        return streamExecutionEnvironment.addSource(DataStreamRMQSource
                .newRabbitMQSource(configurationContainer.getRabbitMQConfiguration()
                        , GiraTravelsSourceModel.QUEUE
                        , deserializationSchema))
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Observable<GiraTravelsSourceModel>>forBoundedOutOfOrderness(Duration.ofMillis(5))
                        .withTimestampAssigner((event, timestamp) -> event.getData().getDateStart().toEpochMilli()))
                .name("GiraTravelsSource");
    }

    private DataStream<Observable<WazeJamsSourceModel>> addWazeJamsSource() {

        DeserializationSchema<Observable<WazeJamsSourceModel>> deserializationSchema = ObservableSourceDeserializer
                .appendObservable(JsonDeserializationSchema.newJsonDeserializationSchema(mapper, TypeInformation.of(WazeJamsSourceModel.class))
                        , WazeJamsSourceModel::getPubMillis);

        return streamExecutionEnvironment.addSource(DataStreamRMQSource
                .newRabbitMQSource(configurationContainer.getRabbitMQConfiguration()
                        , WazeJamsSourceModel.QUEUE
                        , deserializationSchema))
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Observable<WazeJamsSourceModel>>forBoundedOutOfOrderness(Duration.ofMillis(5))
                        .withTimestampAssigner((event, timestamp) -> event.getData().getPubMillis()))
                .name("WazeJamsSource");
    }

    private DataStream<Observable<WazeIrregularitiesSourceModel>> addWazeIrregularitiesSource() {

        DeserializationSchema<Observable<WazeIrregularitiesSourceModel>> deserializationSchema = ObservableSourceDeserializer
                .appendObservable(JsonDeserializationSchema.newJsonDeserializationSchema(mapper, TypeInformation.of(WazeIrregularitiesSourceModel.class))
                        , WazeIrregularitiesSourceModel::getDetectionDateMillis);

        return streamExecutionEnvironment.addSource(DataStreamRMQSource
                .newRabbitMQSource(configurationContainer.getRabbitMQConfiguration()
                        , WazeIrregularitiesSourceModel.QUEUE
                        , deserializationSchema))
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Observable<WazeIrregularitiesSourceModel>>forBoundedOutOfOrderness(Duration.ofMillis(5))
                        .withTimestampAssigner((event, timestamp) -> event.getData().getDetectionDateMillis()))
                .name("WazeIrregularitiesSource");
    }

    public DataStream<Observable<GiraTravelsSourceModel>> getGiraTravelsSource() {
        return giraTravelsSource;
    }

    public DataStream<Observable<WazeJamsSourceModel>> getWazeJamsSource() {
        return wazeJamsSource;
    }

    public DataStream<Observable<WazeIrregularitiesSourceModel>> getWazeIrregularitiesSource() {
        return wazeIrregularitiesSource;
    }
}

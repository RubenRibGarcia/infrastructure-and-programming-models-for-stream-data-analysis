package org.isel.thesis.impads.kafka.stream.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeJamsSourceModel;
import org.isel.thesis.impads.metrics.api.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologySources {

    private static final Logger logger = LoggerFactory.getLogger(TopologySources.class);

    private final KStream<Void, Observable<GiraTravelsSourceModel>> giraTravelsStream;
    private final KStream<Void, Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesStream;
    private final KStream<Void, Observable<WazeJamsSourceModel>> wazeJamsStream;

    private TopologySources(final KStream<Void, Observable<GiraTravelsSourceModel>> giraTravelsStream
            , final KStream<Void, Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesStream
            , final KStream<Void, Observable<WazeJamsSourceModel>> wazeJamsStream) {
        this.giraTravelsStream = giraTravelsStream;
        this.wazeIrregularitiesStream = wazeIrregularitiesStream;
        this.wazeJamsStream = wazeJamsStream;
    }

    public static TopologySources initializeTopologySources(final StreamsBuilder builder
            , final Config conf
            , final ObjectMapper mapper) {

        return new TopologySources(null, null, null);
//                , initializeWazeIrregularitiesSource(builder, conf, mapper)
//                , initializeWazeJamsSource(builder, conf, mapper));
    }

//    private static KStream<Void, IMeasureWrapper<GiraTravelsSourceModel>> initializeGiraTravelsSource(final StreamsBuilder builder
//            , final Config conf
//            , final ObjectMapper mapper) {
////        return builder
////                .<Void, GiraTravelsSourceModel>stream(GiraTravelsSourceModel.KAFKA_TOPIC
////                        , Consumed.<Void, GiraTravelsSourceModel>with(new GiraTravelsTimestampExtractor())
////                                .withKeySerde(Serdes.Void())
////                                .withValueSerde(JsonSerdes.newJsonSerders(mapper, GiraTravelsSourceModel.class)))
////            .mapValues( value ->
////                    MeasureWrapper.of(value
////                        , value.getDateStart().toEpochMilli()
////                        , Instant.now().toEpochMilli()));
//        return builder.stream(GiraTravelsSourceModel.KAFKA_TOPIC);
//    }

//    private static KStream<Void, IMeasureWrapper<WazeIrregularitiesSourceModel>> initializeWazeIrregularitiesSource(final StreamsBuilder builder
//            , final Config conf
//            , final ObjectMapper mapper) {
//        return builder
//                .<Void, WazeIrregularitiesSourceModel>stream(WazeIrregularitiesSourceModel.KAFKA_TOPIC
//                        , Consumed.<Void, WazeIrregularitiesSourceModel>with(new WazeIrregularitiesTimestampExtractor())
//                                .withKeySerde(Serdes.Void())
//                                .withValueSerde(JsonSerdes.newJsonSerders(mapper, WazeIrregularitiesSourceModel.class)))
//                .mapValues( value ->
//                        MeasureWrapper.of(value
//                            , value.getDetectionDateMillis()
//                            , Instant.now().toEpochMilli()));
//    }
//
//    private static KStream<Void, IMeasureWrapper<WazeJamsSourceModel>> initializeWazeJamsSource(final StreamsBuilder builder
//            , final Config conf
//            , final ObjectMapper mapper) {
//        return builder
//                .<Void, WazeJamsSourceModel>stream(WazeJamsSourceModel.KAFKA_TOPIC
//                        , Consumed.<Void, WazeJamsSourceModel>with(new WazeJamsTimestampExtractor())
//                                .withKeySerde(Serdes.Void())
//                                .withValueSerde(JsonSerdes.newJsonSerders(mapper, WazeJamsSourceModel.class)))
//                .mapValues( value ->
//                        MeasureWrapper.of(value
//                            , value.getPubMillis()
//                            , Instant.now().toEpochMilli()));
//    }

    public KStream<Void, Observable<GiraTravelsSourceModel>> getGiraTravelsStream() {
        return giraTravelsStream;
    }

    public KStream<Void, Observable<WazeIrregularitiesSourceModel>> getWazeIrregularitiesStream() {
        return wazeIrregularitiesStream;
    }

    public KStream<Void, Observable<WazeJamsSourceModel>> getWazeJamsStream() {
        return wazeJamsStream;
    }
}

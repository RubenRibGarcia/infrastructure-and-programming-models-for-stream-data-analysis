package org.isel.thesis.impads.kafka.stream.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.isel.thesis.impads.kafka.stream.serdes.JsonSerdes;
import org.isel.thesis.impads.kafka.stream.serdes.ObservableSerdes;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeJamsSourceModel;
import org.isel.thesis.impads.metrics.api.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class TopologySources {

    private static final Logger LOG = LoggerFactory.getLogger(TopologySources.class);

    private final KStream<Void, Observable<GiraTravelsSourceModel>> giraTravelsStream;
    private final KStream<Void, Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesStream;
    private final KStream<Void, Observable<WazeJamsSourceModel>> wazeJamsStream;

    private TopologySources(final KStream<Void, Observable<GiraTravelsSourceModel>> giraTravelsStream
            , final KStream<Void, Observable<WazeJamsSourceModel>> wazeJamsStream
            , final KStream<Void, Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesStream) {
        this.giraTravelsStream = giraTravelsStream;
        this.wazeJamsStream = wazeJamsStream;
        this.wazeIrregularitiesStream = wazeIrregularitiesStream;
    }

    public static TopologySources initializeTopologySources(final StreamsBuilder builder
            , final Config conf
            , final ObjectMapper mapper) {

        return new TopologySources(initializeGiraTravelsSource(builder, conf, mapper)
                , initializeWazeJamsSource(builder, conf, mapper)
                , initializeWazeIrregularitiesSource(builder, conf, mapper));
    }

    private static KStream<Void, Observable<GiraTravelsSourceModel>> initializeGiraTravelsSource(final StreamsBuilder builder
            , final Config config
            , final ObjectMapper mapper) {

        ObservableSerdes<GiraTravelsSourceModel> observableSerdes =
                ObservableSerdes.newObservableSerdes(JsonSerdes.newJsonSerders(mapper, GiraTravelsSourceModel.class)
                        , model -> model.getDateStart().toEpochMilli()
                        , model -> Instant.now().toEpochMilli());

        return builder.stream("gira_travels"
                , Consumed.with(Serdes.Void(), observableSerdes));
    }

    private static KStream<Void, Observable<WazeJamsSourceModel>> initializeWazeJamsSource(final StreamsBuilder builder
            , final Config config
            , final ObjectMapper mapper) {

        ObservableSerdes<WazeJamsSourceModel> observableSerdes =
                ObservableSerdes.newObservableSerdes(JsonSerdes.newJsonSerders(mapper, WazeJamsSourceModel.class)
                        , model -> model.getPubMillis()
                        , model -> Instant.now().toEpochMilli());

        return builder.stream("waze_jams"
                , Consumed.with(Serdes.Void(), observableSerdes));
    }

    private static KStream<Void, Observable<WazeIrregularitiesSourceModel>> initializeWazeIrregularitiesSource(final StreamsBuilder builder
            , final Config config
            , final ObjectMapper mapper) {

        ObservableSerdes<WazeIrregularitiesSourceModel> observableSerdes =
                ObservableSerdes.newObservableSerdes(JsonSerdes.newJsonSerders(mapper, WazeIrregularitiesSourceModel.class)
                        , model -> model.getDetectionDateMillis()
                        , model -> Instant.now().toEpochMilli());

        return builder.stream("waze_jams"
                , Consumed.with(Serdes.Void(), observableSerdes));
    }

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

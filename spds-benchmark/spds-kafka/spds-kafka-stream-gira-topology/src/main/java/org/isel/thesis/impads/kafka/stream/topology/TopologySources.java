package org.isel.thesis.impads.kafka.stream.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.isel.thesis.impads.kafka.stream.serdes.JsonSerdes;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeJamsSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.utils.GiraTravelsTimestampExtractor;
import org.isel.thesis.impads.kafka.stream.topology.utils.WazeIrregularitiesTimestampExtractor;
import org.isel.thesis.impads.kafka.stream.topology.utils.WazeJamsTimestampExtractor;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.metrics.api.Observable;

import java.time.Instant;

public class TopologySources {

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

        return builder.stream("gira_travels"
                , Consumed.with(Serdes.Void()
                        , JsonSerdes.newJsonSerders(mapper, GiraTravelsSourceModel.class)
                        , new GiraTravelsTimestampExtractor()
                        , null))
                .mapValues((k, v) -> ObservableImpl.of(v, v.getDateStart().toEpochMilli(), Instant.now().toEpochMilli()));
    }

    private static KStream<Void, Observable<WazeJamsSourceModel>> initializeWazeJamsSource(final StreamsBuilder builder
            , final Config config
            , final ObjectMapper mapper) {

        return builder.stream("waze_jams"
                , Consumed.with(Serdes.Void()
                        , JsonSerdes.newJsonSerders(mapper, WazeJamsSourceModel.class)
                        , new WazeJamsTimestampExtractor()
                        , null))
                .mapValues((k ,v) -> ObservableImpl.of(v, v.getPubMillis(), Instant.now().toEpochMilli()));
    }

    private static KStream<Void, Observable<WazeIrregularitiesSourceModel>> initializeWazeIrregularitiesSource(final StreamsBuilder builder
            , final Config config
            , final ObjectMapper mapper) {

        return builder.stream("waze_jams"
                , Consumed.with(Serdes.Void()
                        , JsonSerdes.newJsonSerders(mapper, WazeIrregularitiesSourceModel.class)
                        , new WazeIrregularitiesTimestampExtractor()
                        , null))
                .mapValues((k, v) -> ObservableImpl.of(v, v.getDetectionDateMillis(), Instant.now().toEpochMilli()));
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

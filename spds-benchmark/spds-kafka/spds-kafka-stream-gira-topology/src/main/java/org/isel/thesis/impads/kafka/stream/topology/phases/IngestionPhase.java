package org.isel.thesis.impads.kafka.stream.topology.phases;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.serdes.JsonSerdes;
import org.isel.thesis.impads.kafka.stream.topology.ConfigurationContainer;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeJamsSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.utils.GiraTravelsTimestampExtractor;
import org.isel.thesis.impads.kafka.stream.topology.utils.WazeIrregularitiesTimestampExtractor;
import org.isel.thesis.impads.kafka.stream.topology.utils.WazeJamsTimestampExtractor;
import org.isel.thesis.impads.metrics.Observable;

import java.io.Serializable;
import java.time.Instant;

public class IngestionPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final StreamsBuilder streamsBuilder;
    private final ConfigurationContainer configurationContainer;
    private final ObjectMapper mapper;
    private final KafkaStreamObservableMetricsCollector collector;

    private KStream<Void, Observable<GiraTravelsSourceModel>> giraTravelsSource;
    private KStream<Void, Observable<WazeJamsSourceModel>> wazeJamsSource;
    private KStream<Void, Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesSource;

    public IngestionPhase(final StreamsBuilder streamsBuilder
            , final ConfigurationContainer configurationContainer
            , final ObjectMapper mapper
            , final KafkaStreamObservableMetricsCollector collector) {
        this.streamsBuilder = streamsBuilder;
        this.configurationContainer = configurationContainer;
        this.mapper = mapper;
        this.collector = collector;

        initializePhase();
    }

    private void initializePhase() {
        this.giraTravelsSource = addGiraTravelsSource();
        this.wazeJamsSource = addWazeJamsSource();
        this.wazeIrregularitiesSource = addWazeIrregularitiesSource();

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.INGESTION) {
            giraTravelsSource.peek((k,v) -> collector.collect(v));
            wazeJamsSource.peek((k,v) -> collector.collect(v));
            wazeIrregularitiesSource.peek((k,v) -> collector.collect(v));
        }
    }

    private KStream<Void, Observable<GiraTravelsSourceModel>> addGiraTravelsSource() {

        return streamsBuilder.stream("gira_travels"
                , Consumed.with(Serdes.Void()
                        , JsonSerdes.newJsonSerders(mapper, GiraTravelsSourceModel.class)
                        , new GiraTravelsTimestampExtractor()
                        , null).withName("gira_travels_source"))
                .mapValues((k, v) -> Observable.of(v, v.getDateStart().toEpochMilli(), Instant.now().toEpochMilli())
                        , Named.as("observable_gira_travels_map"));
    }

    private KStream<Void, Observable<WazeJamsSourceModel>> addWazeJamsSource() {

        return streamsBuilder.stream("waze_jams"
                , Consumed.with(Serdes.Void()
                        , JsonSerdes.newJsonSerders(mapper, WazeJamsSourceModel.class)
                        , new WazeJamsTimestampExtractor()
                        , null).withName("waze_jams_source"))
                .mapValues((k ,v) -> Observable.of(v, v.getPubMillis(), Instant.now().toEpochMilli())
                        , Named.as("observable_waze_jams_map"));
    }

    private KStream<Void, Observable<WazeIrregularitiesSourceModel>> addWazeIrregularitiesSource() {

        return streamsBuilder.stream("waze_irregularities"
                , Consumed.with(Serdes.Void()
                        , JsonSerdes.newJsonSerders(mapper, WazeIrregularitiesSourceModel.class)
                        , new WazeIrregularitiesTimestampExtractor()
                        , null).withName("waze_irregularities_source"))
                .mapValues((k, v) -> Observable.of(v, v.getDetectionDateMillis(), Instant.now().toEpochMilli())
                        , Named.as("observable_waze_irregularities_map"));
    }

    public KStream<Void, Observable<GiraTravelsSourceModel>> getGiraTravelsSource() {
        return giraTravelsSource;
    }

    public KStream<Void, Observable<WazeJamsSourceModel>> getWazeJamsSource() {
        return wazeJamsSource;
    }

    public KStream<Void, Observable<WazeIrregularitiesSourceModel>> getWazeIrregularitiesSource() {
        return wazeIrregularitiesSource;
    }
}

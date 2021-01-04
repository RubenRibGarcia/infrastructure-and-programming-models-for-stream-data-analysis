package org.isel.thesis.impads.kafka.stream.topology.phases;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.topology.ConfigurationContainer;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeJamsSourceModel;
import org.isel.thesis.impads.metrics.Observable;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ParsePhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConfigurationContainer configurationContainer;
    private final KafkaStreamObservableMetricsCollector collector;

    private KStream<Long, ObservableSimplifiedGiraTravelsModel> simplifiedGiraTravelsStream;
    private KStream<Long, ObservableSimplifiedWazeJamsModel> simplifiedWazeJamsStream;
    private KStream<Long, ObservableSimplifiedWazeIrregularitiesModel> simplifiedWazeIrregularitiesStream;

    public ParsePhase(final ConfigurationContainer configurationContainer
            , final KafkaStreamObservableMetricsCollector collector
            , final IngestionPhase ingestionPhase) {
        this.configurationContainer = configurationContainer;
        this.collector = collector;

        initializePhase(ingestionPhase);
    }

    private void initializePhase(final IngestionPhase ingestionPhase) {
        this.simplifiedGiraTravelsStream = parseGiraTravels(ingestionPhase.getGiraTravelsSource());
        this.simplifiedWazeJamsStream = parseWazeJams(ingestionPhase.getWazeJamsSource());
        this.simplifiedWazeIrregularitiesStream = parseWazeIrregularities(ingestionPhase.getWazeIrregularitiesSource());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.PARSE) {
            simplifiedGiraTravelsStream.peek((k,v) -> collector.collect(v));
            simplifiedWazeJamsStream.peek((k,v) -> collector.collect(v));
            simplifiedWazeIrregularitiesStream.peek((k,v) -> collector.collect(v));
        }
    }

    private KStream<Long, ObservableSimplifiedGiraTravelsModel> parseGiraTravels(KStream<Void, Observable<GiraTravelsSourceModel>> giraTravelsSource) {
        return giraTravelsSource
                .filter((k, v) ->
                        (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty())
                                || v.getData().getNumberOfVertices() != null && v.getData().getNumberOfVertices() > 1
                                || v.getData().getDistance() != null && v.getData().getDistance() > 0)
                .map((k, v) -> KeyValue.pair(Instant.ofEpochMilli(v.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli(), new ObservableSimplifiedGiraTravelsModel(v.map(new SimplifiedGiraTravelsModel(String.valueOf(v.getData().getId())
                        , v.getData().getGeometry()
                        , v.getEventTimestamp())))));
    }

    private KStream<Long, ObservableSimplifiedWazeJamsModel> parseWazeJams(KStream<Void, Observable<WazeJamsSourceModel>> wazeJamsSource) {
        return wazeJamsSource
                .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty()))
                .map((k, v) -> KeyValue.pair(Instant.ofEpochMilli(v.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli(), new ObservableSimplifiedWazeJamsModel(v.map(new SimplifiedWazeJamsModel(String.valueOf(v.getData().getId())
                        , v.getData().getGeometry()
                        , v.getEventTimestamp())))));
    }

    private KStream<Long, ObservableSimplifiedWazeIrregularitiesModel> parseWazeIrregularities(KStream<Void, Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesSource) {
        return wazeIrregularitiesSource
                .filter((k, v) -> (v.getData().getGeometry() != null && !v.getData().getGeometry().isEmpty()))
                .map((k, v) -> KeyValue.pair(Instant.ofEpochMilli(v.getEventTimestamp()).truncatedTo(ChronoUnit.SECONDS).toEpochMilli(), new ObservableSimplifiedWazeIrregularitiesModel(v.map(new SimplifiedWazeIrregularitiesModel(String.valueOf(v.getData().getId())
                        , v.getData().getGeometry()
                        , v.getEventTimestamp())))));
    }

    public KStream<Long, ObservableSimplifiedGiraTravelsModel> getSimplifiedGiraTravelsStream() {
        return simplifiedGiraTravelsStream;
    }

    public KStream<Long, ObservableSimplifiedWazeJamsModel> getSimplifiedWazeJamsStream() {
        return simplifiedWazeJamsStream;
    }

    public KStream<Long, ObservableSimplifiedWazeIrregularitiesModel> getSimplifiedWazeIrregularitiesStream() {
        return simplifiedWazeIrregularitiesStream;
    }
}

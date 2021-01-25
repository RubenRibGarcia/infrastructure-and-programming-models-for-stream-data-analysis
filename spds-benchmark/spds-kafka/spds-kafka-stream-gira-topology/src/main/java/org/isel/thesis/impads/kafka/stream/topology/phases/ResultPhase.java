package org.isel.thesis.impads.kafka.stream.topology.phases;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.isel.thesis.impads.kafka.stream.metrics.KafkaStreamObservableMetricsCollector;
import org.isel.thesis.impads.kafka.stream.topology.ConfigurationContainer;
import org.isel.thesis.impads.kafka.stream.topology.model.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableGiraTravelsWithWazeResults;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeAndIpma;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.WKBReader;

import java.io.Serializable;

public class ResultPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final double GEOMETRY_BUFFER = 0.0005;

    private final ConfigurationContainer configurationContainer;
    private final GeometryFactory geoFactory;
    private final KafkaStreamObservableMetricsCollector collector;

    private KStream<Long, ObservableGiraTravelsWithWazeResults> resultStream;

    public ResultPhase(final ConfigurationContainer configurationContainer
            , final GeometryFactory geoFactory
            , final KafkaStreamObservableMetricsCollector collector
            , StaticJoinPhase staticJoinPhase) {
        this.configurationContainer = configurationContainer;
        this.geoFactory = geoFactory;
        this.collector = collector;

        initializePhase(staticJoinPhase);
    }

    private void initializePhase(StaticJoinPhase staticJoinPhase) {
        this.resultStream = transformToResult(staticJoinPhase.getEnrichedJoinedGiraTravelsWithWazeAndIpma());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.RESULT) {
            resultStream.peek((k,v) -> collector.collect(v));
        }
    }

    private KStream<Long, ObservableGiraTravelsWithWazeResults> transformToResult(
            KStream<Long, ObservableJoinedGiraTravelsWithWazeAndIpma> enrichedJoinedGiraTravelsWithWazeAndIpma) {



        return enrichedJoinedGiraTravelsWithWazeAndIpma
                .map((k, v) -> {
                    try {
                        final WKBReader reader = new WKBReader(geoFactory);

                        boolean jamAndIrrMatches = false;

                        final Geometry giraGeo
                                = reader.read(WKBReader.hexToBytes(v.getData().getFirst().getGeometry()));
                        final Geometry wazeIrrGeo
                                = reader.read(WKBReader.hexToBytes(v.getData().getThird().getGeometry()));
                        final Geometry wazeJamGeo
                                = reader.read(WKBReader.hexToBytes(v.getData().getSecond().getGeometry()));

                        final Geometry giraTravelStartingPoint =
                                ((LineString) giraGeo.getGeometryN(0))
                                        .getStartPoint()
                                        .buffer(GEOMETRY_BUFFER);

                        if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
                            jamAndIrrMatches = true;
                        }

                        GiraTravelsWithWazeAndIpmaResult rvalue = new GiraTravelsWithWazeAndIpmaResult(v.getData().getFirst()
                                , v.getData().getSecond()
                                , v.getData().getThird()
                                , v.getData().getFourth()
                                , giraTravelStartingPoint.intersects(wazeJamGeo)
                                , giraTravelStartingPoint.intersects(wazeIrrGeo)
                                , jamAndIrrMatches);

                        return KeyValue.pair(k, new ObservableGiraTravelsWithWazeResults(v.map(rvalue)));
                    }
                    catch(Exception e) {
                        GiraTravelsWithWazeAndIpmaResult rvalue = new GiraTravelsWithWazeAndIpmaResult(v.getData().getFirst()
                                , v.getData().getSecond()
                                , v.getData().getThird()
                                , v.getData().getFourth()
                                , false
                                , false
                                , false);
                        return KeyValue.pair(k, new ObservableGiraTravelsWithWazeResults(v.map(rvalue)));
                    }
                });
    }

    public KStream<Long, ObservableGiraTravelsWithWazeResults> getResultStream() {
        return resultStream;
    }
}

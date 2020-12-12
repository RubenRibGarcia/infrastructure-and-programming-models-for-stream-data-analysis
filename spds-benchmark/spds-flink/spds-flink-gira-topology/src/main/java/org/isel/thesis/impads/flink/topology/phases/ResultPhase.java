package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.Observable;
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

    private DataStream<Observable<GiraTravelsWithWazeAndIpmaResult>> resultStream;

    public ResultPhase(final ConfigurationContainer configurationContainer
            , final GeometryFactory geoFactory
            , StaticJoinPhase staticJoinPhase) {
        this.configurationContainer = configurationContainer;
        this.geoFactory = geoFactory;

        initializePhase(staticJoinPhase);
    }

    private void initializePhase(StaticJoinPhase staticJoinPhase) {
        this.resultStream =
                transformToResult(staticJoinPhase.getEnrichedJoinedGiraTravelsWithWazeAndIpma());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.INITIAL_TRANSFORMATION) {
            resultStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
        }
    }

    private DataStream<Observable<GiraTravelsWithWazeAndIpmaResult>> transformToResult(DataStream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> enrichedJoinedGiraTravelsWithWazeAndIpma) {
        return enrichedJoinedGiraTravelsWithWazeAndIpma.map(new MapFunction<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>, Observable<GiraTravelsWithWazeAndIpmaResult>>() {
            @Override
            public Observable<GiraTravelsWithWazeAndIpmaResult> map(Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> tuple) throws Exception {
                boolean jamAndIrrMatches = false;

                WKBReader reader = new WKBReader(geoFactory);
                final Geometry giraGeo
                        = reader.read(WKBReader.hexToBytes(tuple.getData().f0.getGeometry()));
                final Geometry wazeIrrGeo
                        = reader.read(WKBReader.hexToBytes(tuple.getData().f2.getGeometry()));
                final Geometry wazeJamGeo
                        = reader.read(WKBReader.hexToBytes(tuple.getData().f1.getGeometry()));

                final Geometry giraTravelStartingPoint =
                        ((LineString) giraGeo.getGeometryN(0))
                                .getStartPoint()
                                .buffer(GEOMETRY_BUFFER);

                if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
                    jamAndIrrMatches = true;
                }

                GiraTravelsWithWazeAndIpmaResult rvalue = new GiraTravelsWithWazeAndIpmaResult(tuple.getData().f0
                        , tuple.getData().f1
                        , tuple.getData().f2
                        , tuple.getData().f3
                        , giraTravelStartingPoint.intersects(wazeJamGeo)
                        , giraTravelStartingPoint.intersects(wazeIrrGeo)
                        , jamAndIrrMatches);

                return tuple.map(rvalue);
            }
        });
    }

    public DataStream<Observable<GiraTravelsWithWazeAndIpmaResult>> getResultStream() {
        return resultStream;
    }
}

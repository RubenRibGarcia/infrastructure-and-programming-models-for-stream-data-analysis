package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.function.ResultMapFunction;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.Observable;
import org.locationtech.jts.geom.GeometryFactory;

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
                transformToResult(geoFactory
                        , staticJoinPhase.getEnrichedJoinedGiraTravelsWithWazeAndIpma());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.RESULT) {
            resultStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
        }
    }

    private static DataStream<Observable<GiraTravelsWithWazeAndIpmaResult>> transformToResult(GeometryFactory geoFactory
            , DataStream<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> enrichedJoinedGiraTravelsWithWazeAndIpma) {
        return enrichedJoinedGiraTravelsWithWazeAndIpma.map(new ResultMapFunction(geoFactory));
    }

    public DataStream<Observable<GiraTravelsWithWazeAndIpmaResult>> getResultStream() {
        return resultStream;
    }
}

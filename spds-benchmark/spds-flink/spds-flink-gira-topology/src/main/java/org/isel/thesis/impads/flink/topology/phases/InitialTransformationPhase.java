package org.isel.thesis.impads.flink.topology.phases;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.isel.thesis.impads.flink.metrics.ObservableSinkFunction;
import org.isel.thesis.impads.flink.topology.ConfigurationContainer;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.flink.topology.models.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.flink.topology.models.WazeJamsSourceModel;
import org.isel.thesis.impads.metrics.Observable;

import java.io.Serializable;

public class InitialTransformationPhase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ConfigurationContainer configurationContainer;

    private DataStream<Observable<SimplifiedGiraTravelsModel>> simplifiedGiraTravelsStream;
    private DataStream<Observable<SimplifiedWazeJamsModel>> simplifiedWazeJamsStream;
    private DataStream<Observable<SimplifiedWazeIrregularitiesModel>> simplifiedWazeIrregularitiesStream;

    public InitialTransformationPhase(final ConfigurationContainer configurationContainer
            , final IngestionPhase ingestionPhase) {
        this.configurationContainer = configurationContainer;

        initializePhase(ingestionPhase);
    }

    private void initializePhase(final IngestionPhase ingestionPhase) {
        this.simplifiedGiraTravelsStream = parseGiraTravels(ingestionPhase.getGiraTravelsSource());
        this.simplifiedWazeJamsStream = parseWazeJams(ingestionPhase.getWazeJamsSource());
        this.simplifiedWazeIrregularitiesStream = parseWazeIrregularities(ingestionPhase.getWazeIrregularitiesSource());

        Phases untilPhase = configurationContainer.getTopologyConfiguration().getUntilPhase();

        if (untilPhase == Phases.INITIAL_TRANSFORMATION) {
            simplifiedGiraTravelsStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
            simplifiedWazeJamsStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
            simplifiedWazeIrregularitiesStream.addSink(ObservableSinkFunction.observe(configurationContainer.getMetricsCollectorConfiguration()));
        }
    }

    private DataStream<Observable<SimplifiedGiraTravelsModel>> parseGiraTravels(DataStream<Observable<GiraTravelsSourceModel>> giraTravelsSource) {
        return giraTravelsSource
                        .filter(model -> model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty()
                                || model.getData().getNumberOfVertices() != null && model.getData().getNumberOfVertices() > 1
                                || model.getData().getDistance() != null && model.getData().getDistance() > 0)
                        .map(new MapFunction<Observable<GiraTravelsSourceModel>
                                , Observable<SimplifiedGiraTravelsModel>>() {
                            @Override
                            public Observable<SimplifiedGiraTravelsModel> map(
                                    Observable<GiraTravelsSourceModel> model) throws Exception {

                                return model.map(new SimplifiedGiraTravelsModel(String.valueOf(model.getData().getId())
                                        , model.getData().getGeometry()
                                        , model.getEventTimestamp()));
                            }
                        });
    }

    private DataStream<Observable<SimplifiedWazeJamsModel>> parseWazeJams(DataStream<Observable<WazeJamsSourceModel>> wazeJamsSource) {
        return wazeJamsSource
                        .filter(model ->
                                model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty())
                        .map(new MapFunction<Observable<WazeJamsSourceModel>
                                , Observable<SimplifiedWazeJamsModel>>() {
                            @Override
                            public Observable<SimplifiedWazeJamsModel> map(
                                    Observable<WazeJamsSourceModel> model) throws Exception {

                                return model.map(new SimplifiedWazeJamsModel(String.valueOf(model.getData().getId())
                                        , model.getData().getGeometry()
                                        , model.getEventTimestamp()));
                            }
                        });
    }

    private DataStream<Observable<SimplifiedWazeIrregularitiesModel>> parseWazeIrregularities(DataStream<Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesSource) {
        return wazeIrregularitiesSource
                        .filter(model ->
                                model.getData().getGeometry() != null && !model.getData().getGeometry().isEmpty())
                        .map(new MapFunction<Observable<WazeIrregularitiesSourceModel>
                                , Observable<SimplifiedWazeIrregularitiesModel>>() {
                            @Override
                            public Observable<SimplifiedWazeIrregularitiesModel> map(
                                    Observable<WazeIrregularitiesSourceModel> model) throws Exception {

                                return model.map(new SimplifiedWazeIrregularitiesModel(String.valueOf(model.getData().getId())
                                        , model.getData().getGeometry()
                                        , model.getEventTimestamp()));
                            }
                        });
    }

    public DataStream<Observable<SimplifiedGiraTravelsModel>> getSimplifiedGiraTravelsStream() {
        return simplifiedGiraTravelsStream;
    }

    public DataStream<Observable<SimplifiedWazeJamsModel>> getSimplifiedWazeJamsStream() {
        return simplifiedWazeJamsStream;
    }

    public DataStream<Observable<SimplifiedWazeIrregularitiesModel>> getSimplifiedWazeIrregularitiesStream() {
        return simplifiedWazeIrregularitiesStream;
    }
}

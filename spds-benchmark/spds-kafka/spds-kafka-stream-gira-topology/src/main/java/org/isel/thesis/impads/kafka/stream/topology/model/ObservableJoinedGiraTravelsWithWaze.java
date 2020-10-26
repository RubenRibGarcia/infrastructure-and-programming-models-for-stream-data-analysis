package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.kafka.stream.data.structures.Tuple2;
import org.isel.thesis.impads.kafka.stream.data.structures.Tuple3;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.metrics.api.Observable;

public class ObservableJoinedGiraTravelsWithWaze
        extends ObservableImpl<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> {

    public ObservableJoinedGiraTravelsWithWaze(Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

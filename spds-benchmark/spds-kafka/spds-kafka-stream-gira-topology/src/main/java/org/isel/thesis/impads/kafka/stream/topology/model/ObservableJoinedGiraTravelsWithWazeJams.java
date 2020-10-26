package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.kafka.stream.data.structures.Tuple2;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.metrics.api.Observable;

public class ObservableJoinedGiraTravelsWithWazeJams extends ObservableImpl<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> {

    public ObservableJoinedGiraTravelsWithWazeJams(Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

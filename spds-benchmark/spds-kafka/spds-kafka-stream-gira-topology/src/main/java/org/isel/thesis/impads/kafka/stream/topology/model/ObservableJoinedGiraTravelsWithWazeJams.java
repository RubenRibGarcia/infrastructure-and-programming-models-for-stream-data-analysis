package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.structures.Tuple2;

public class ObservableJoinedGiraTravelsWithWazeJams extends Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> {

    public ObservableJoinedGiraTravelsWithWazeJams(Observable<Tuple2<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel>> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.structures.Tuple3;

public class ObservableJoinedGiraTravelsWithWaze
        extends Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> {

    public ObservableJoinedGiraTravelsWithWaze(Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

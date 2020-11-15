package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.kafka.stream.data.structures.Tuple3;
import org.isel.thesis.impads.metrics.Observable;

public class ObservableJoinedGiraTravelsWithWaze
        extends Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> {

    public ObservableJoinedGiraTravelsWithWaze(Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

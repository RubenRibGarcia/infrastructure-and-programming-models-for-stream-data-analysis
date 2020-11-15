package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.metrics.Observable;

public class ObservableSimplifiedWazeIrregularitiesModel extends Observable<SimplifiedWazeIrregularitiesModel> {

    public ObservableSimplifiedWazeIrregularitiesModel(Observable<SimplifiedWazeIrregularitiesModel> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.metrics.Observable;

public class ObservableSimplifiedWazeJamsModel extends Observable<SimplifiedWazeJamsModel> {

    public ObservableSimplifiedWazeJamsModel(Observable<SimplifiedWazeJamsModel> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

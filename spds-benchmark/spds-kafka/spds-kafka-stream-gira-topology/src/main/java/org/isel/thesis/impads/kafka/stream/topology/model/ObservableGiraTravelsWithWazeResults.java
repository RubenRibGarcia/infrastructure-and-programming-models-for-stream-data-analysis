package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.metrics.Observable;

public class ObservableGiraTravelsWithWazeResults
        extends Observable<GiraTravelsWithWazeResult> {

    public ObservableGiraTravelsWithWazeResults(Observable<GiraTravelsWithWazeResult> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

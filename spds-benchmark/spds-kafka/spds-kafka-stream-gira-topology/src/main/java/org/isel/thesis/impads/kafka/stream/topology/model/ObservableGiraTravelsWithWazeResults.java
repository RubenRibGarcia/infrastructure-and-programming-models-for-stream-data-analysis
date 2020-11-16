package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.metrics.Observable;

public class ObservableGiraTravelsWithWazeResults
        extends Observable<GiraTravelsWithWazeAndIpmaResult> {

    public ObservableGiraTravelsWithWazeResults(Observable<GiraTravelsWithWazeAndIpmaResult> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

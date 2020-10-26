package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.kafka.stream.data.structures.Tuple3;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.metrics.api.Observable;

public class ObservableGiraTravelsWithWazeResults
        extends ObservableImpl<GiraTravelsWithWazeResult> {

    public ObservableGiraTravelsWithWazeResults(Observable<GiraTravelsWithWazeResult> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

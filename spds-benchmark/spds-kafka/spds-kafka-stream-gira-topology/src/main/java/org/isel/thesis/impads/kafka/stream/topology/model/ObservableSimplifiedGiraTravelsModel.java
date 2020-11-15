package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.metrics.Observable;

public class ObservableSimplifiedGiraTravelsModel extends Observable<SimplifiedGiraTravelsModel> {

    public ObservableSimplifiedGiraTravelsModel(Observable<SimplifiedGiraTravelsModel> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

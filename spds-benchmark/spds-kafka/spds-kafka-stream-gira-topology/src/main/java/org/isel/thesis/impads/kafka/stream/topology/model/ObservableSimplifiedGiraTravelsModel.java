package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.metrics.api.Observable;

public class ObservableSimplifiedGiraTravelsModel extends ObservableImpl<SimplifiedGiraTravelsModel> {

    public ObservableSimplifiedGiraTravelsModel(Observable<SimplifiedGiraTravelsModel> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

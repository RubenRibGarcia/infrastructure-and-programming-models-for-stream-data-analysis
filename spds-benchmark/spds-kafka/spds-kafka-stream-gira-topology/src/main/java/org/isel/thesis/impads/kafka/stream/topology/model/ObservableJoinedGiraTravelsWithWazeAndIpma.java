package org.isel.thesis.impads.kafka.stream.topology.model;

import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.structures.Tuple4;

public class ObservableJoinedGiraTravelsWithWazeAndIpma
        extends Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> {

    public ObservableJoinedGiraTravelsWithWazeAndIpma(Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> observable) {
        super(observable.getData()
                , observable.getEventTimestamp()
                , observable.getIngestionTimestamp()
                , observable.getProcessedTimestamp());
    }
}

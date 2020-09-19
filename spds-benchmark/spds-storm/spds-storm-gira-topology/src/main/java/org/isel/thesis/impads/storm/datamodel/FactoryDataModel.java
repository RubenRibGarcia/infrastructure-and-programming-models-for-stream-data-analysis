package org.isel.thesis.impads.storm.datamodel;

import org.isel.thesis.impads.storm.sourcemodel.gira.GiraTravelsSourceModel;
import org.isel.thesis.impads.storm.sourcemodel.waze.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.storm.sourcemodel.waze.WazeJamsSourceModel;

import java.io.Serializable;

public final class FactoryDataModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public static SimplifiedGiraTravelsModel from(GiraTravelsSourceModel model) {
        return new SimplifiedGiraTravelsModel(model.getId().toString(), model.getGeometry());
    }

    public static SimplifiedWazeJamsModel from(WazeJamsSourceModel model) {
        return new SimplifiedWazeJamsModel(model.getId().toString(), model.getGeometry());
    }

    public static SimplifiedWazeIrregularitiesModel from(WazeIrregularitiesSourceModel model) {
        return new SimplifiedWazeIrregularitiesModel(model.getId().toString(), model.getGeometry());
    }
}

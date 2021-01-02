package org.isel.thesis.impads.flink.topology.function;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.Observable;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.WKBReader;

public class ResultMapFunction
        extends RichMapFunction<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>
        , Observable<GiraTravelsWithWazeAndIpmaResult>> {

    private static final long serialVersionUID = 1L;

    private static final double GEOMETRY_BUFFER = 0.0005;

    private final GeometryFactory geometryFactory;
    private transient WKBReader wkbReader;

    public ResultMapFunction(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.wkbReader = new WKBReader(geometryFactory);
    }

    @Override
    public Observable<GiraTravelsWithWazeAndIpmaResult> map(
            Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> tuple)
            throws Exception {

        boolean jamAndIrrMatches = false;

        final Geometry giraGeo
                = wkbReader.read(WKBReader.hexToBytes(tuple.getData().f0.getGeometry()));
        final Geometry wazeIrrGeo
                = wkbReader.read(WKBReader.hexToBytes(tuple.getData().f2.getGeometry()));
        final Geometry wazeJamGeo
                = wkbReader.read(WKBReader.hexToBytes(tuple.getData().f1.getGeometry()));

        final Geometry giraTravelStartingPoint =
                ((LineString) giraGeo.getGeometryN(0))
                        .getStartPoint()
                        .buffer(GEOMETRY_BUFFER);

        if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
            jamAndIrrMatches = true;
        }

        GiraTravelsWithWazeAndIpmaResult rvalue = new GiraTravelsWithWazeAndIpmaResult(tuple.getData().f0
                , tuple.getData().f1
                , tuple.getData().f2
                , tuple.getData().f3
                , giraTravelStartingPoint.intersects(wazeJamGeo)
                , giraTravelStartingPoint.intersects(wazeIrrGeo)
                , jamAndIrrMatches);

        return tuple.map(rvalue);
    }
}

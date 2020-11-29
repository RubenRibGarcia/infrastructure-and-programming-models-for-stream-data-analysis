package org.isel.thesis.impads.storm.topology.bolts.processor;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.storm.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.structures.Tuple4;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.WKBReader;

public class GiraTravelsWithWazeAndIpmaResultProcessor implements ParserProcessor {

    private static final double GEOMETRY_BUFFER = 0.0005;

    private final GeometryFactory geometryFactory;

    public GiraTravelsWithWazeAndIpmaResultProcessor(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    @Override
    public void process(Tuple tuple, OutputCollector collector) {
        Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> model =
                (Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>) tuple.getValueByField("value");

        try {
            boolean jamAndIrrMatches = false;

            WKBReader reader = new WKBReader(geometryFactory);
            final Geometry giraGeo
                    = reader.read(WKBReader.hexToBytes(model.getData().getFirst().getGeometry()));
            final Geometry wazeIrrGeo
                    = reader.read(WKBReader.hexToBytes(model.getData().getThird().getGeometry()));
            final Geometry wazeJamGeo
                    = reader.read(WKBReader.hexToBytes(model.getData().getSecond().getGeometry()));

            final Geometry giraTravelStartingPoint =
                    ((LineString) giraGeo.getGeometryN(0))
                            .getStartPoint()
                            .buffer(GEOMETRY_BUFFER);

            if (wazeIrrGeo.equalsExact(wazeJamGeo, GEOMETRY_BUFFER)) {
                jamAndIrrMatches = true;
            }

            GiraTravelsWithWazeAndIpmaResult rvalue = new GiraTravelsWithWazeAndIpmaResult(model.getData().getFirst()
                    , model.getData().getSecond()
                    , model.getData().getThird()
                    , model.getData().getFourth()
                    , giraTravelStartingPoint.intersects(wazeJamGeo)
                    , giraTravelStartingPoint.intersects(wazeIrrGeo)
                    , jamAndIrrMatches);

            collector.emit(new Values(model.map(rvalue)));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Fields outputFields() {
        return new Fields("value");
    }
}

package org.isel.thesis.impads.storm.topology.bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
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

import java.util.Map;

public class ResultMapBolt implements IRichBolt {

    private static final double GEOMETRY_BUFFER = 0.0005;

    private final GeometryFactory geometryFactory;
    private WKBReader reader;
    private OutputCollector outputCollector;

    public ResultMapBolt(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        this.reader = new WKBReader(geometryFactory);
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> model =
                    (Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>) tuple.getValueByField("value");

            boolean jamAndIrrMatches = false;

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

            outputCollector.emit(new Values(model.map(rvalue)));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("value"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}

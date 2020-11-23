package org.isel.thesis.impads.storm.streams.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.streams.Stream;
import org.apache.storm.streams.StreamBuilder;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.ConfigurationContainer;
import org.isel.thesis.impads.storm.spouts.rabbitmq.RMQSpout;
import org.isel.thesis.impads.storm.spouts.rabbitmq.func.JsonToTupleProducer;
import org.isel.thesis.impads.storm.streams.topology.models.GiraTravelsSourceModel;
import org.isel.thesis.impads.storm.streams.topology.models.GiraTravelsSourceModel.GiraTravelsTupleMapper;
import org.isel.thesis.impads.storm.streams.topology.models.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.storm.streams.topology.models.WazeIrregularitiesSourceModel.WazeIrregularitiesTupleMapper;
import org.isel.thesis.impads.storm.streams.topology.models.WazeJamsSourceModel;
import org.isel.thesis.impads.storm.streams.topology.models.WazeJamsSourceModel.WazeJamsTupleMapper;

public final class TopologyStreamSources {

    private final Stream<Observable<GiraTravelsSourceModel>> giraTravelsSourceModelStream;
    private final Stream<Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesSourceModelStream;
    private final Stream<Observable<WazeJamsSourceModel>> wazeJamsSourceModelStream;

    private TopologyStreamSources(final Stream<Observable<GiraTravelsSourceModel>> giraTravelsSourceModelStream
            , final Stream<Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesSourceModelStream
            , final Stream<Observable<WazeJamsSourceModel>> wazeJamsSourceModelStream) {

        this.giraTravelsSourceModelStream = giraTravelsSourceModelStream;
        this.wazeIrregularitiesSourceModelStream = wazeIrregularitiesSourceModelStream;
        this.wazeJamsSourceModelStream = wazeJamsSourceModelStream;
    }

    //TODO: Broken Topology sources
    public static TopologyStreamSources initializeTopologySources(ConfigurationContainer config
            , ObjectMapper mapper
            , StreamBuilder streamBuilder) {

        final Stream<Observable<GiraTravelsSourceModel>> giraTravelStream = streamBuilder.newStream(RMQSpout.newRabbitMQSpout(config.getRabbitMQConfiguration()
                , "gira_travels"
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , GiraTravelsSourceModel.class)), GiraTravelsTupleMapper.map());

        final Stream<Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesStream = streamBuilder.newStream(RMQSpout.newRabbitMQSpout(config.getRabbitMQConfiguration()
                , "waze_irregularities"
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , WazeIrregularitiesSourceModel.class)), WazeIrregularitiesTupleMapper.map());

        final Stream<Observable<WazeJamsSourceModel>> wazeJamsStream = streamBuilder.newStream(RMQSpout.newRabbitMQSpout(config.getRabbitMQConfiguration()
                , "waze_jams"
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , WazeJamsSourceModel.class)), WazeJamsTupleMapper.map());

        return new TopologyStreamSources(giraTravelStream
                , wazeIrregularitiesStream
                , wazeJamsStream);
    }

    public Stream<Observable<GiraTravelsSourceModel>> getGiraTravelsSourceModelStream() {
        return giraTravelsSourceModelStream;
    }

    public Stream<Observable<WazeIrregularitiesSourceModel>> getWazeIrregularitiesSourceModelStream() {
        return wazeIrregularitiesSourceModelStream;
    }

    public Stream<Observable<WazeJamsSourceModel>> getWazeJamsSourceModelStream() {
        return wazeJamsSourceModelStream;
    }
}

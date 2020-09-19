package org.isel.thesis.impads.storm.streams.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.streams.Stream;
import org.apache.storm.streams.StreamBuilder;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.storm.spouts.rabbitmq.RabbitMQSpout;
import org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.spouts.rabbitmq.func.JsonToTupleProducer;
import org.isel.thesis.impads.storm.sourcemodel.gira.GiraTravelsSourceModel;
import org.isel.thesis.impads.storm.sourcemodel.gira.GiraTravelsSourceModel.GiraTravelsTupleMapper;
import org.isel.thesis.impads.storm.sourcemodel.waze.WazeIrregularitiesSourceModel;
import org.isel.thesis.impads.storm.sourcemodel.waze.WazeIrregularitiesSourceModel.WazeIrregularitiesTupleMapper;
import org.isel.thesis.impads.storm.sourcemodel.waze.WazeJamsSourceModel;
import org.isel.thesis.impads.storm.sourcemodel.waze.WazeJamsSourceModel.WazeJamsTupleMapper;

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

    public static TopologyStreamSources initializeTopologySources(RabbitMQConfiguration rabbitMQConfiguration
            , ObjectMapper mapper
            , StreamBuilder streamBuilder) {

        final Stream<Observable<GiraTravelsSourceModel>> giraTravelStream = streamBuilder.newStream(RabbitMQSpout.newRabbitMQSpout(rabbitMQConfiguration
                , GiraTravelsSourceModel.QUEUE
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , GiraTravelsSourceModel.class
                        , GiraTravelsSourceModel.getTupleField())), GiraTravelsTupleMapper.map());

        final Stream<Observable<WazeIrregularitiesSourceModel>> wazeIrregularitiesStream = streamBuilder.newStream(RabbitMQSpout.newRabbitMQSpout(rabbitMQConfiguration
                , WazeIrregularitiesSourceModel.QUEUE
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , WazeIrregularitiesSourceModel.class
                        , WazeIrregularitiesSourceModel.getTupleField())), WazeIrregularitiesTupleMapper.map());

        final Stream<Observable<WazeJamsSourceModel>> wazeJamsStream = streamBuilder.newStream(RabbitMQSpout.newRabbitMQSpout(rabbitMQConfiguration
                , WazeJamsSourceModel.QUEUE
                , true
                , JsonToTupleProducer.jsonTupleProducer(mapper
                        , WazeJamsSourceModel.class
                        , WazeJamsSourceModel.getTupleField())), WazeJamsTupleMapper.map());

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

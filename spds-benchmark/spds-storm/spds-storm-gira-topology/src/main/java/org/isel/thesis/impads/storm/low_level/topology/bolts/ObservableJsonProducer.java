package org.isel.thesis.impads.storm.low_level.topology.bolts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.metrics.api.SerializableEventTimestampAssigner;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.ITupleProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

public class ObservableJsonProducer<T> implements ITupleProducer, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(ObservableJsonProducer.class);

    private static final long serialVersionUID = 1L;

    private final ObjectMapper mapper;
    private final Class<T> klass;
    private final SerializableEventTimestampAssigner<T> eventTimestampAssigner;

    private ObservableJsonProducer(ObjectMapper mapper
            , Class<T> klass
            , SerializableEventTimestampAssigner<T> eventTimestampAssigner) {
        this.mapper = mapper;
        this.klass = klass;
        this.eventTimestampAssigner = eventTimestampAssigner;
    }

    public static <T> ObservableJsonProducer<T> observableJsonTupleProducer(ObjectMapper mapper
            , Class<T> klass
            , SerializableEventTimestampAssigner<T> eventTimestampAssigner) {
        return new ObservableJsonProducer<>(mapper, klass, eventTimestampAssigner);
    }

    @Override
    public Values toTuple(byte[] message) {
        try {
            T data = mapper.readValue(message, klass);

            return new Values(Observable.of(data
                    , eventTimestampAssigner.extractEventTimestamp(data)
                    , Instant.now().toEpochMilli()));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer, Optional<String> streamId) {
        if (streamId.isPresent()) {
            declarer.declareStream(streamId.get(), new Fields("value"));
        }
        else {
            declarer.declare(new Fields("value"));
        }
    }
}

package org.isel.thesis.impads.storm.spouts.rabbitmq.func;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.ITupleProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

public class JsonToTupleProducer<T> implements ITupleProducer, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(JsonToTupleProducer.class);

    private static final long serialVersionUID = 1L;

    private final ObjectMapper mapper;
    private final Class<T> fromKlass;

    private JsonToTupleProducer(ObjectMapper mapper
            , Class<T> fromKlass) {
        this.mapper = mapper;
        this.fromKlass = fromKlass;
    }

    public static <T> JsonToTupleProducer<T> jsonTupleProducer(ObjectMapper mapper
            , Class<T> klass) {
        return new JsonToTupleProducer<>(mapper, klass);
    }

    @Override
    public Values toTuple(byte[] message) {
        try {
            T data = mapper.readValue(message, fromKlass);
            return new Values(data);
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

package org.isel.thesis.impads.storm.spouts.rabbitmq.func;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.IJsonTuple;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.ITupleProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

public class JsonToTupleProducer<T extends IJsonTuple> implements ITupleProducer, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(JsonToTupleProducer.class);

    private static final long serialVersionUID = 1L;

    private final ObjectMapper mapper;
    private final Class<T> klass;

    private Fields delaredFields;

    private JsonToTupleProducer(ObjectMapper mapper
            , Class<T> klass
            , Fields delaredFields) {
        this.mapper = mapper;
        this.klass = klass;
        this.delaredFields = delaredFields;
    }

    public static <T extends IJsonTuple> JsonToTupleProducer<T> jsonTupleProducer(ObjectMapper mapper
            , Class<T> klass
            , Fields declaredField) {
        return new JsonToTupleProducer<>(mapper, klass, declaredField);
    }

    @Override
    public Values toTuple(byte[] message) {
        try {
            T data = mapper.readValue(message, klass);
            return data.getTupleValues();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer, Optional<String> streamId) {
        if (streamId.isPresent()) {
            declarer.declareStream(streamId.get(), delaredFields);
        }
        else {
            declarer.declare(delaredFields);
        }
    }
}

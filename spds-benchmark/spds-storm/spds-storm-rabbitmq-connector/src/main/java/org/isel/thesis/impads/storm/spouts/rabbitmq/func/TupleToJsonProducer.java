package org.isel.thesis.impads.storm.spouts.rabbitmq.func;

import org.apache.storm.tuple.ITuple;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.ITupleMapper;

public class TupleToJsonProducer implements ITupleMapper {
    @Override
    public String getKeyFromTuple(ITuple tuple) {
        return null;
    }

    @Override
    public byte[] getValueFromTuple(ITuple tuple) {
        return null;
    }
}

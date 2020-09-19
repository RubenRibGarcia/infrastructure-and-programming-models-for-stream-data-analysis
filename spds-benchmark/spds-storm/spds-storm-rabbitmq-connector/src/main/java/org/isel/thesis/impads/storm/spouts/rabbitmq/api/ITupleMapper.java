package org.isel.thesis.impads.storm.spouts.rabbitmq.api;

import org.apache.storm.tuple.ITuple;

public interface ITupleMapper {

    String getKeyFromTuple(ITuple tuple);

    byte[] getValueFromTuple(ITuple tuple);
}

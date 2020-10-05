package org.isel.thesis.impads.storm.spouts.rabbitmq.api;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.Optional;

public interface ITupleProducer extends Serializable {

    Values toTuple(byte[] message);

    void declareOutputFields(OutputFieldsDeclarer declarer, Optional<String> streamId);
}

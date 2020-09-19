package org.isel.thesis.impads.storm.spouts.rabbitmq.api;

import org.apache.storm.shade.com.google.common.base.Optional;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Values;

import java.io.Serializable;

public interface ITupleProducer extends Serializable {

    Values toTuple(byte[] message);

    void declareOutputFields(OutputFieldsDeclarer declarer, Optional<String> streamId);
}

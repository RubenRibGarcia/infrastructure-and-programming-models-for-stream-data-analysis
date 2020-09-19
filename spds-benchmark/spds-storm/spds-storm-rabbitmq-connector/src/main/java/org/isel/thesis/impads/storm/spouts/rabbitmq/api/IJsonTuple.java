package org.isel.thesis.impads.storm.spouts.rabbitmq.api;

import org.apache.storm.tuple.Values;

public interface IJsonTuple {

    Values getTupleValues();
}

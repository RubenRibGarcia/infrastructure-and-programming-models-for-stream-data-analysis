package org.isel.thesis.impads.storm.topology.bolts.processor;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.io.Serializable;

public interface ParserProcessor extends Serializable {
    void process(Tuple tuple, OutputCollector collector);

    Fields outputFields();
}

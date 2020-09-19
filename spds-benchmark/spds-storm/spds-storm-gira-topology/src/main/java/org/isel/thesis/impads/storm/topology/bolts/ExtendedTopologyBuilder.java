package org.isel.thesis.impads.storm.topology.bolts;

import org.apache.storm.lambda.SerializableBiConsumer;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

public final class ExtendedTopologyBuilder extends TopologyBuilder {

    public ExtendedTopologyBuilder() {
        super();
    }

    public BoltDeclarer setBolt(String id
            , SerializableBiConsumer<Tuple, BasicOutputCollector> triConsumer
            , String streamId
            , Fields fields
            , Number parallelismHint) {
        return setBolt(id, new StreamLambdaFunctionBolt(triConsumer, streamId, fields), parallelismHint);
    }

    public BoltDeclarer setBolt(String id
            , SerializableBiConsumer<Tuple, BasicOutputCollector> triConsumer
            , String streamId
            , Fields fields) {
        return setBolt(id, new StreamLambdaFunctionBolt(triConsumer, streamId, fields));
    }
}

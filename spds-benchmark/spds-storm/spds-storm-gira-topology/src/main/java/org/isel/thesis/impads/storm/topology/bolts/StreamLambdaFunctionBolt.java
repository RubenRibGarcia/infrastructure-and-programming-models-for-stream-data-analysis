package org.isel.thesis.impads.storm.topology.bolts;

import org.apache.storm.lambda.SerializableBiConsumer;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

public class StreamLambdaFunctionBolt extends BaseBasicBolt {

    private final SerializableBiConsumer<Tuple, BasicOutputCollector> biConsumer;

    private final Fields fields;
    private final String streamId;

    public StreamLambdaFunctionBolt(SerializableBiConsumer<Tuple, BasicOutputCollector> biConsumer
            , String streamId
            , Fields fields) {
        this.biConsumer = biConsumer;
        this.streamId = streamId;
        this.fields = fields;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream(streamId, fields);
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        biConsumer.accept(input, collector);
    }
}

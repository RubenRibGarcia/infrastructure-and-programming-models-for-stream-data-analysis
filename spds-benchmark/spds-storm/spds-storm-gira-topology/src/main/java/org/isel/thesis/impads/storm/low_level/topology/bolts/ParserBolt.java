package org.isel.thesis.impads.storm.low_level.topology.bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.isel.thesis.impads.storm.low_level.topology.bolts.processor.ParserProcessor;

import java.util.Map;

public class ParserBolt implements IRichBolt {

    private OutputCollector outputCollector;
    private final ParserProcessor processor;

    private ParserBolt(ParserProcessor processor) {
        this.processor = processor;
    }

    public static ParserBolt parse(ParserProcessor processor) {
        return new ParserBolt(processor);
    }

    @Override
    public void prepare(Map<String, Object> map
            , TopologyContext topologyContext
            , OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        this.processor.process(tuple, outputCollector);
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(this.processor.outputFields());
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}

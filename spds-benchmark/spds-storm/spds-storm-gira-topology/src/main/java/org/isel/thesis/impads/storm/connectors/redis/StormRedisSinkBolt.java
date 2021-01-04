package org.isel.thesis.impads.storm.connectors.redis;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.isel.thesis.impads.connectors.redis.RedisWriterFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class StormRedisSinkBolt<T> implements IRichBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(StormRedisSinkBolt.class);

    protected final RedisWriterFunction<T> writerFunction;
    protected final TupleMapper<T> tupleMapper;

    public StormRedisSinkBolt(TupleMapper<T> tupleMapper
            , RedisWriterFunction<T> writerFunction) {
        this.tupleMapper = tupleMapper;
        this.writerFunction = writerFunction;
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Sink - No output fields
    }

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        try {
            this.writerFunction.open();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void cleanup() {
        try {
            this.writerFunction.close();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}

package org.isel.thesis.impads.storm.connectors.redis;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class StormRedisHashCacheableMapperBolt<IN, T, R> implements IRichBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(StormRedisHashCacheableMapperBolt.class);

    protected OutputCollector outputCollector;

    protected RedisHashCacheableMapFunction<T, R> function;

    protected final TupleMapper<IN> tupleMapper;
    private final String[] outputFields;

    public StormRedisHashCacheableMapperBolt(RedisHashCacheableMapFunction<T, R> function
            , TupleMapper<IN> tupleMapper
            , String... outputFields) {
        this.function = function;
        this.tupleMapper = tupleMapper;
        this.outputFields = outputFields;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(outputFields));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        try {
            this.function.open();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void cleanup() {
        try {
            this.function.close();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}

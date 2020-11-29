package org.isel.thesis.impads.storm.redis.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;
import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainer;
import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainerBuilder;

import java.util.Map;

public abstract class BaseRedisBolt implements IRichBolt {

    private static final long serialVersionUID = 1L;

    private final JedisPoolConfig config;

    private TopologyContext context;
    private OutputCollector collector;

    private RedisCommandsContainer container;

    protected BaseRedisBolt(JedisPoolConfig config) {
        this.config = config;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.container = RedisCommandsContainerBuilder.build(config);
        this.context = context;
        this.collector = collector;
    }

    @Override
    public void cleanup() {

    }

    public RedisCommandsContainer getContainer() {
        return container;
    }

    public OutputCollector getCollector() {
        return collector;
    }

    public TopologyContext getContext() {
        return context;
    }
}

package org.isel.thesis.impads.storm.redis.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;
import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainer;
import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainerBuilder;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisCommand;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisCommandDescription;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisLookup;

import java.util.Map;

public class RedisLookupBolt implements IRichBolt {

    private final JedisPoolConfig config;
    private final RedisLookup lookup;

    private final RedisCommand redisCommand;

    private TopologyContext context;
    private OutputCollector collector;

    private RedisCommandsContainer container;

    public RedisLookupBolt(JedisPoolConfig config, RedisLookup lookup) {
        this.config = config;
        this.lookup = lookup;

        RedisCommandDescription redisCommandDescription = lookup.getCommandDescription();

        this.redisCommand = redisCommandDescription.getCommand();
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
    public void execute(Tuple input) {
        String key = lookup.getKeyFromTuple(input);
        String hashField = lookup.getHashFieldFromTuple(input);

        String rvalue;
        switch (redisCommand) {
            case HGET:
                rvalue = container.hget(key, hashField);
                break;
            default:
                throw new IllegalArgumentException("Cannot process such command: " + redisCommand);
        }

        collector.emit(new Values(rvalue));
    }

    @Override
    public void cleanup() {

    }
}

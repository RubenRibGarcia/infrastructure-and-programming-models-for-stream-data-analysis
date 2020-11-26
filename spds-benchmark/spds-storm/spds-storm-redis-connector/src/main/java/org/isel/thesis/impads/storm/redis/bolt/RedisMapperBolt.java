package org.isel.thesis.impads.storm.redis.bolt;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.storm.redis.common.Transform;
import org.isel.thesis.impads.storm.redis.common.TupleMapper;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;

public class RedisMapperBolt<T> extends BaseRedisBolt {

    private final TupleMapper<T> tupleMapper;
    private final Transform<T, Values> transform;
    private final String[] outputFields;

    public RedisMapperBolt(JedisPoolConfig config
            , TupleMapper<T> tupleMapper
            , Transform<T, Values> transform
            , String... outputFields) {
        super(config);
        this.tupleMapper = tupleMapper;
        this.transform = transform;
        this.outputFields = outputFields;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(outputFields));
    }

    @Override
    public void execute(Tuple tuple) {
        getCollector().emit(transform.apply(getContainer(), tupleMapper.apply(tuple)));
    }
}

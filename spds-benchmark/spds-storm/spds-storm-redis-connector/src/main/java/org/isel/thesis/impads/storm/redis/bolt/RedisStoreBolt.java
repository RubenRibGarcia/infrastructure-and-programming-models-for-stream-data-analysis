package org.isel.thesis.impads.storm.redis.bolt;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.isel.thesis.impads.storm.redis.common.Consume;
import org.isel.thesis.impads.storm.redis.common.TupleMapper;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;

public class RedisStoreBolt<T> extends BaseRedisBolt {

    private static final long serialVersionUID = 1L;

    private final TupleMapper<T> tupleMapper;
    private final Consume<T> consume;

    public RedisStoreBolt(JedisPoolConfig config
            , TupleMapper<T> tupleMapper
            , Consume<T> consume) {
        super(config);
        this.tupleMapper = tupleMapper;
        this.consume = consume;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public void execute(Tuple tuple) {
        consume.accept(getContainer(), tupleMapper.apply(tuple));
    }
}

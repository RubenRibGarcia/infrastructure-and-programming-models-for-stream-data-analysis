package org.isel.thesis.impads.storm.redis.bolt;

import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.storm.redis.common.Consume;
import org.isel.thesis.impads.storm.redis.common.Transform;
import org.isel.thesis.impads.storm.redis.common.TupleMapper;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;

public final class RedisBoltBuilder {

    private RedisBoltBuilder() { }

    public static <T> RedisMapperBoltBuilder<T> mapper(JedisPoolConfig jedisPoolConfig) {
        return new RedisMapperBoltBuilder<>(jedisPoolConfig);
    }

    public static <T> RedisStoreBoltBuilder<T> store(JedisPoolConfig jedisPoolConfig) {
        return new RedisStoreBoltBuilder<>(jedisPoolConfig);
    }

    public static final class RedisMapperBoltBuilder<T> {

        private final JedisPoolConfig jedisPoolConfig;
        private TupleMapper<T> tupleMapper;
        private Transform<T, Values> transform;
        private String[] outputFields;

        private RedisMapperBoltBuilder(JedisPoolConfig jedisPoolConfig) {
            this.jedisPoolConfig = jedisPoolConfig;
        }

        public RedisMapperBoltBuilder<T> tupleMapper(TupleMapper<T> tupleMapper) {
            this.tupleMapper = tupleMapper;
            return this;
        }

        public RedisMapperBoltBuilder<T> transform(Transform<T, Values> transform) {
            this.transform = transform;
            return this;
        }

        public RedisMapperBoltBuilder<T> outputFields(String... outputFields) {
            this.outputFields = outputFields;
            return this;
        }

        public RedisMapperBolt build() {
            return new RedisMapperBolt<>(jedisPoolConfig
                    , tupleMapper
                    , transform
                    , outputFields);
        }
    }

    public static final class RedisStoreBoltBuilder<T> {

        private final JedisPoolConfig jedisPoolConfig;
        private TupleMapper<T> tupleMapper;
        private Consume<T> consume;

        private RedisStoreBoltBuilder(JedisPoolConfig jedisPoolConfig) {
            this.jedisPoolConfig = jedisPoolConfig;
        }

        public RedisStoreBoltBuilder<T> tupleMapper(TupleMapper<T> tupleMapper) {
            this.tupleMapper = tupleMapper;
            return this;
        }

        public RedisStoreBoltBuilder<T> consume(Consume<T> consume) {
            this.consume = consume;
            return this;
        }

        public RedisStoreBolt<T> build() {
            return new RedisStoreBolt<>(jedisPoolConfig
                    , tupleMapper
                    , consume);
        }
    }
}

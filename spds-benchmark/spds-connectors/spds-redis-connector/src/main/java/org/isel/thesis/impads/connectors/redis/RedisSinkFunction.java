package org.isel.thesis.impads.connectors.redis;

import org.isel.thesis.impads.connectors.redis.api.RedisConsumer;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class RedisSinkFunction<IN>
        extends BaseRedisFunction<IN, Void>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisSinkFunction.class);

    private RedisConsumer<IN> consumer;

    private RedisSinkFunction(RedisConfigurationBase redisConfigurationBase
            , RedisConsumer<IN> consumer) {
        super(redisConfigurationBase);
        this.consumer = consumer;
    }

    public static <IN> RedisSinkFunction<IN> sink(RedisConfigurationBase redisConfigurationBase
            , RedisConsumer<IN> function) {
        return new RedisSinkFunction<>(redisConfigurationBase, function);
    }

    public void sink(IN input) throws Exception {
        try {
            consumer.consume(commands, input);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }
}

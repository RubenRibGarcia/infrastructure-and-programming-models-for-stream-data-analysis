package org.isel.thesis.impads.connectors.redis;

import org.isel.thesis.impads.connectors.redis.api.RedisConsumer;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.function.Consumer;

public class RedisWriterFunction<I>
        extends BaseRedisFunction
        implements Consumer<I>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisWriterFunction.class);

    private final RedisConsumer<I> consumer;

    private RedisWriterFunction(RedisConfigurationBase redisConfigurationBase
            , RedisConsumer<I> consumer) {
        super(redisConfigurationBase);
        this.consumer = consumer;
    }

    public static <I> RedisWriterFunction<I> newWriter(RedisConfigurationBase redisConfigurationBase
            , RedisConsumer<I> function) {
        return new RedisWriterFunction<>(redisConfigurationBase, function);
    }

    @Override
    public void accept(I i) {
        consumer.consume(commands, i);
    }
}

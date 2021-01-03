package org.isel.thesis.impads.connectors.redis;

import org.isel.thesis.impads.connectors.redis.api.RedisWriter;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class RedisWriterFunction<I>
        extends BaseRedisFunction
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisWriterFunction.class);

    private RedisWriter<I> writer;

    public RedisWriterFunction() { }

    private RedisWriterFunction(RedisConfigurationBase redisConfigurationBase
            , RedisWriter<I> writer) {
        super(redisConfigurationBase);
        this.writer = writer;
    }

    public static <I> RedisWriterFunction<I> newWriter(RedisConfigurationBase redisConfigurationBase
            , RedisWriter<I> writer) {
        return new RedisWriterFunction<>(redisConfigurationBase, writer);
    }

    public void write(I i) {
        writer.write(commands, i);
    }
}

package org.isel.thesis.impads.kafka.stream.connectors.redis.common.container;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.isel.thesis.impads.kafka.stream.connectors.redis.common.config.JedisPoolConfig;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

/**
 * The builder for {@link RedisCommandsContainer}.
 */
public class RedisCommandsContainerBuilder {

    public static RedisCommandsContainer build(JedisPoolConfig jedisPoolConfig) {
        Objects.requireNonNull(jedisPoolConfig, "Redis pool config should not be Null");

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(jedisPoolConfig.getMaxIdle());
        genericObjectPoolConfig.setMaxTotal(jedisPoolConfig.getMaxTotal());
        genericObjectPoolConfig.setMinIdle(jedisPoolConfig.getMinIdle());

        JedisPool jedisPool = new JedisPool(genericObjectPoolConfig, jedisPoolConfig.getHost(),
            jedisPoolConfig.getPort(), jedisPoolConfig.getConnectionTimeout());
        return new RedisContainer(jedisPool);
    }
}

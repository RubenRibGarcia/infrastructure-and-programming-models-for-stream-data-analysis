package org.isel.thesis.impads.kafka.connect.redis.container;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.isel.thesis.impads.kafka.connect.redis.conf.RedisConfiguration;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

/**
 * The builder for {@link RedisCommandsContainer}.
 */
public class RedisCommandsContainerBuilder {

    public static RedisCommandsContainer build(RedisConfiguration redisConfiguration) {
        Objects.requireNonNull(redisConfiguration, "Redis pool config should not be Null");

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(redisConfiguration.getRedisMaxIdle());
        genericObjectPoolConfig.setMaxTotal(redisConfiguration.getRedisMaxTotal());
        genericObjectPoolConfig.setMinIdle(redisConfiguration.getRedisMinIdle());

        JedisPool jedisPool = new JedisPool(genericObjectPoolConfig, redisConfiguration.getRedisHost(),
                redisConfiguration.getRedisPort());
        return new RedisContainer(jedisPool);
    }
}

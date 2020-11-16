package org.isel.thesis.impads.kafka.stream.connectors.redis.common.container;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.isel.thesis.impads.kafka.stream.connectors.redis.common.config.JedisConfigBase;
import org.isel.thesis.impads.kafka.stream.connectors.redis.common.config.JedisPoolConfig;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Objects;

/**
 * The builder for {@link RedisCommandsContainer}.
 */
public class RedisCommandsContainerBuilder {

    public static RedisCommandsContainer build(JedisConfigBase jedisConfigBase){
        if(jedisConfigBase instanceof JedisPoolConfig){
            JedisPoolConfig flinkJedisPoolConfig = (JedisPoolConfig) jedisConfigBase;
            return RedisCommandsContainerBuilder.build(flinkJedisPoolConfig);
        } else {
            throw new IllegalArgumentException("Jedis configuration not found");
        }
    }

    public static RedisCommandsContainer build(JedisPoolConfig jedisPoolConfig) {
        Objects.requireNonNull(jedisPoolConfig, "Redis pool config should not be Null");

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(jedisPoolConfig.getMaxIdle());
        genericObjectPoolConfig.setMaxTotal(jedisPoolConfig.getMaxTotal());
        genericObjectPoolConfig.setMinIdle(jedisPoolConfig.getMinIdle());

        JedisPool jedisPool = new JedisPool(genericObjectPoolConfig, jedisPoolConfig.getHost(),
            jedisPoolConfig.getPort(), jedisPoolConfig.getConnectionTimeout(), jedisPoolConfig.getPassword(),
            jedisPoolConfig.getDatabase());
        return new RedisContainer(jedisPool);
    }
}

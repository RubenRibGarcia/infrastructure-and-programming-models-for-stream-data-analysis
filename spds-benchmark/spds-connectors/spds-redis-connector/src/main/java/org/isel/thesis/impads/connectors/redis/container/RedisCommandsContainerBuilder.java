package org.isel.thesis.impads.connectors.redis.container;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.isel.thesis.impads.connectors.redis.common.RedisConfiguration;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationBase;

public final class RedisCommandsContainerBuilder {

    public static RedisCommandsContainer build(RedisConfigurationBase config) {
        if (config.isMocked()) {
            return new MockedRedisContainer();
        }
        else if (config instanceof RedisConfiguration) {
            return build((RedisConfiguration) config);
        }
        else {
            throw new IllegalArgumentException("Redis configuration not found");
        }
    }

    private static RedisContainer build(RedisConfiguration config) {
        RedisClient redisClient = RedisClient.create(RedisURI.create(config.getHost(), config.getPort()));

        return new RedisContainer(redisClient);
    }
}

package org.isel.thesis.impads.connectors.redis.common;

public final class RedisConfigurationFields {
    private static final String REDIS_PREFIX = "redis.";
    public static final String REDIS_MOCKED = REDIS_PREFIX.concat("mocked");
    public static final String REDIS_HOST = REDIS_PREFIX.concat("host");
    public static final String REDIS_PORT = REDIS_PREFIX.concat("port");
}

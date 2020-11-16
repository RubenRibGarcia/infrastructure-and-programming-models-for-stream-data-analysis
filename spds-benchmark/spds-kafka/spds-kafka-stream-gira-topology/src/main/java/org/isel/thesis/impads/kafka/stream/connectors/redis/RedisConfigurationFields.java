package org.isel.thesis.impads.kafka.stream.connectors.redis;

public final class RedisConfigurationFields {
    private static final String REDIS_PREFIX = "redis.";
    public static final String REDIS_HOST = REDIS_PREFIX.concat("host");
    public static final String REDIS_PORT = REDIS_PREFIX.concat("port");
    public static final String REDIS_MAX_IDLE = REDIS_PREFIX.concat("max_idle");
    public static final String REDIS_MAX_TOTAL = REDIS_PREFIX.concat("max_total");
    public static final String REDIS_MIN_IDLE = REDIS_PREFIX.concat("min_idle");
}

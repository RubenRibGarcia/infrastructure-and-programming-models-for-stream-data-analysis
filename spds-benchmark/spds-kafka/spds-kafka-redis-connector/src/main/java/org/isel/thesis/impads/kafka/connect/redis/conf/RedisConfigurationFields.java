package org.isel.thesis.impads.kafka.connect.redis.conf;

public final class RedisConfigurationFields {

    private static final String REDIS_PREFIX = "redis.";
    public static final String REDIS_HOST = REDIS_PREFIX + "host";
    public static final String REDIS_PORT = REDIS_PREFIX + "port";
    public static final String REDIS_MAX_IDLE = REDIS_PREFIX + "max_idle";
    public static final String REDIS_MAX_TOTAL = REDIS_PREFIX + "max_total";
    public static final String REDIS_MIN_IDLE = REDIS_PREFIX + "min_idle";
    public static final String REDIS_CONNECTION_TIMEOUT_MS = REDIS_PREFIX + "connection_timeout_ms";
    public static final String REDIS_DATABASE = REDIS_PREFIX + "database";
    public static final String REDIS_PASSWORD = REDIS_PREFIX + "password";
    public static final String REDIS_COMMAND = REDIS_PREFIX + "command";
    public static final String REDIS_KEY = REDIS_PREFIX + "key";
}

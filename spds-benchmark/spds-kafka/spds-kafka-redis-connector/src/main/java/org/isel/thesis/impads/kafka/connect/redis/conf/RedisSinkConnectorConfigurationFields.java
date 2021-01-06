package org.isel.thesis.impads.kafka.connect.redis.conf;

public final class RedisSinkConnectorConfigurationFields {

    private static final String REDIS_PREFIX = "redis.";
    public static final String REDIS_MOCKED = REDIS_PREFIX + "mocked";
    public static final String REDIS_HOST = REDIS_PREFIX + "host";
    public static final String REDIS_PORT = REDIS_PREFIX + "port";
    public static final String REDIS_COMMAND = REDIS_PREFIX + "command";
    public static final String REDIS_KEY = REDIS_PREFIX + "key";
}

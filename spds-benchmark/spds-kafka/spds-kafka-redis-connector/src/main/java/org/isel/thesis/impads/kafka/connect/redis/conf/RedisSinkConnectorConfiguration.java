package org.isel.thesis.impads.kafka.connect.redis.conf;

import org.apache.kafka.common.config.AbstractConfig;

public class RedisSinkConnectorConfiguration {

    private final AbstractConfig config;

    private RedisSinkConnectorConfiguration(AbstractConfig config) {
        this.config = config;
    }

    public static RedisSinkConnectorConfiguration load(AbstractConfig config) {
        return new RedisSinkConnectorConfiguration(config);
    }

    public String getRedisHost() {
        return config.getString(RedisConfigurationFields.REDIS_HOST);
    }

    public int getRedisPort() {
        return config.getInt(RedisConfigurationFields.REDIS_PORT);
    }

    public String getRedisDatabase() {
        return config.getString(RedisConfigurationFields.REDIS_DATABASE);
    }

    public String getRedisPassword() {
        return config.getString(RedisConfigurationFields.REDIS_PASSWORD);
    }

    public int getRedisMaxTotal() {
        return config.getInt(RedisConfigurationFields.REDIS_MAX_TOTAL);
    }

    public int getRedisMaxIdle() {
        return config.getInt(RedisConfigurationFields.REDIS_MAX_IDLE);
    }

    public int getRedisMinIdle() {
        return config.getInt(RedisConfigurationFields.REDIS_MIN_IDLE);
    }

    public String getRedisCommand() {
        return config.getString(RedisConfigurationFields.REDIS_COMMAND);
    }

    public String getRedisKey() {
        return config.getString(RedisConfigurationFields.REDIS_KEY);
    }

    public int getRedisConnectionTimeoutMs() {
        return config.getInt(RedisConfigurationFields.REDIS_CONNECTION_TIMEOUT_MS);
    }
}

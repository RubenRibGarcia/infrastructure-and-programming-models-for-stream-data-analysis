package org.isel.thesis.impads.kafka.connect.redis.conf;

import java.util.Map;

public class RedisConfiguration {

    private final Map<String, String> config;

    private RedisConfiguration(Map<String, String> config) {
        this.config = config;
    }

    public static RedisConfiguration load(Map<String, String> config) {
        return new RedisConfiguration(config);
    }

    public String getRedisHost() {
        return config.get(RedisConfigurationFields.REDIS_HOST);
    }

    public int getRedisPort() {
        return Integer.parseInt(config.get(RedisConfigurationFields.REDIS_PORT));
    }

    public String getRedisDatabase() {
        return config.get(RedisConfigurationFields.REDIS_DATABASE);
    }

    public String getRedisPassword() {
        return config.get(RedisConfigurationFields.REDIS_PASSWORD);
    }

    public int getRedisMaxTotal() {
        return Integer.parseInt(config.get(RedisConfigurationFields.REDIS_MAX_TOTAL));
    }

    public int getRedisMaxIdle() {
        return Integer.parseInt(config.get(RedisConfigurationFields.REDIS_MAX_IDLE));
    }

    public int getRedisMinIdle() {
        return Integer.parseInt(config.get(RedisConfigurationFields.REDIS_MIN_IDLE));
    }

    public String getRedisCommand() {
        return config.get(RedisConfigurationFields.REDIS_COMMAND);
    }

    public String getRedisKey() {
        return config.get(RedisConfigurationFields.REDIS_KEY);
    }

    public int getRedisConnectionTimeoutMs() {
        return Integer.parseInt(config.get(RedisConfigurationFields.REDIS_CONNECTION_TIMEOUT_MS));
    }

}

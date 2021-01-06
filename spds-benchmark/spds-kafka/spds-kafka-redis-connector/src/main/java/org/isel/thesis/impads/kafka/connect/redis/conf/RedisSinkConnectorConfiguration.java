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
        return config.getString(RedisSinkConnectorConfigurationFields.REDIS_HOST);
    }

    public int getRedisPort() {
        return config.getInt(RedisSinkConnectorConfigurationFields.REDIS_PORT);
    }

    public String getRedisCommand() {
        return config.getString(RedisSinkConnectorConfigurationFields.REDIS_COMMAND);
    }

    public String getRedisKey() {
        return config.getString(RedisSinkConnectorConfigurationFields.REDIS_KEY);
    }

    public boolean isMocked() {
        return config.getBoolean(RedisSinkConnectorConfigurationFields.REDIS_MOCKED);
    }
    
}

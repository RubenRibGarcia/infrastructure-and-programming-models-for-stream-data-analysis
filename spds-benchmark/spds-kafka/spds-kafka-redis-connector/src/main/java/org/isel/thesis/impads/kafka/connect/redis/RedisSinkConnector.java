package org.isel.thesis.impads.kafka.connect.redis;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.isel.thesis.impads.kafka.connect.redis.conf.RedisConfigurationFields;
import org.isel.thesis.impads.kafka.connect.redis.conf.RedisSinkConnectorConfiguration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RedisSinkConnector extends SinkConnector {

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(RedisConfigurationFields.REDIS_HOST, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Redis Host")
            .define(RedisConfigurationFields.REDIS_PORT, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Redis Port")
            .define(RedisConfigurationFields.REDIS_CONNECTION_TIMEOUT_MS, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Redis Connection Timeout Ms")
            .define(RedisConfigurationFields.REDIS_MIN_IDLE, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Redis Pool Min Idle")
            .define(RedisConfigurationFields.REDIS_MAX_IDLE, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Redis Pool Max Idle")
            .define(RedisConfigurationFields.REDIS_MAX_TOTAL, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Redis Pool Max Total")
            .define(RedisConfigurationFields.REDIS_COMMAND, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Redis Command")
            .define(RedisConfigurationFields.REDIS_KEY, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Redis Key");

    private RedisSinkConnectorConfiguration conf;

    @Override
    public void start(Map<String, String> map) {
        AbstractConfig parsedConfig = new AbstractConfig(CONFIG_DEF, map);
        this.conf = RedisSinkConnectorConfiguration.load(parsedConfig);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return RedisSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        List<Map<String, String>> configs = new LinkedList<>();

        Map<String, String> config = new HashMap<>();
        config.put(RedisConfigurationFields.REDIS_HOST, this.conf.getRedisHost());
        config.put(RedisConfigurationFields.REDIS_PORT, String.valueOf(this.conf.getRedisPort()));
        config.put(RedisConfigurationFields.REDIS_CONNECTION_TIMEOUT_MS, String.valueOf(this.conf.getRedisConnectionTimeoutMs()));
        config.put(RedisConfigurationFields.REDIS_MAX_TOTAL, String.valueOf(this.conf.getRedisMaxTotal()));
        config.put(RedisConfigurationFields.REDIS_MAX_IDLE, String.valueOf(this.conf.getRedisMaxIdle()));
        config.put(RedisConfigurationFields.REDIS_MIN_IDLE, String.valueOf(this.conf.getRedisMinIdle()));
        config.put(RedisConfigurationFields.REDIS_COMMAND, this.conf.getRedisCommand());
        config.put(RedisConfigurationFields.REDIS_KEY, this.conf.getRedisKey());

        configs.add(config);
        return configs;
    }

    @Override
    public void stop() {
        //Nothing to do here
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }
}

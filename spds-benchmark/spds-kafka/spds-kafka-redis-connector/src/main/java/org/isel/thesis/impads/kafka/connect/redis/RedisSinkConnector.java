package org.isel.thesis.impads.kafka.connect.redis;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.isel.thesis.impads.kafka.connect.redis.conf.RedisSinkConnectorConfigurationFields;
import org.isel.thesis.impads.kafka.connect.redis.conf.RedisSinkConnectorConfiguration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RedisSinkConnector extends SinkConnector {

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(RedisSinkConnectorConfigurationFields.REDIS_MOCKED, ConfigDef.Type.BOOLEAN, ConfigDef.Importance.HIGH, "Redis Mocked")
            .define(RedisSinkConnectorConfigurationFields.REDIS_HOST, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Redis Host")
            .define(RedisSinkConnectorConfigurationFields.REDIS_PORT, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Redis Port")
            .define(RedisSinkConnectorConfigurationFields.REDIS_COMMAND, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Redis Command")
            .define(RedisSinkConnectorConfigurationFields.REDIS_KEY, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Redis Key");

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
        config.put(RedisSinkConnectorConfigurationFields.REDIS_MOCKED, Boolean.toString(this.conf.isMocked()));
        config.put(RedisSinkConnectorConfigurationFields.REDIS_HOST, this.conf.getRedisHost());
        config.put(RedisSinkConnectorConfigurationFields.REDIS_PORT, String.valueOf(this.conf.getRedisPort()));
        config.put(RedisSinkConnectorConfigurationFields.REDIS_COMMAND, this.conf.getRedisCommand());
        config.put(RedisSinkConnectorConfigurationFields.REDIS_KEY, this.conf.getRedisKey());

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

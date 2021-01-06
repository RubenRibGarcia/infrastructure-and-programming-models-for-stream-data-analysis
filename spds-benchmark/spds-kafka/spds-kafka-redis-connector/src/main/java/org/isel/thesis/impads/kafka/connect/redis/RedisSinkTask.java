package org.isel.thesis.impads.kafka.connect.redis;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.isel.thesis.impads.connectors.redis.common.RedisConfiguration;
import org.isel.thesis.impads.connectors.redis.container.RedisCommandsContainerBuilder;
import org.isel.thesis.impads.connectors.redis.container.RedisWriteCommandsContainer;
import org.isel.thesis.impads.kafka.connect.redis.conf.RedisSinkConnectorConfigurationFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class RedisSinkTask extends SinkTask {

    private static final Logger LOG = LoggerFactory.getLogger(RedisSinkTask.class);

    private RedisConfiguration config;
    private String key;
    private RedisCommands command;
    private RedisWriteCommandsContainer commandsContainer;

    @Override
    public String version() {
        return new RedisSinkConnector().version();
    }

    @Override
    public void start(Map<String, String> map) {
        this.config = RedisConfiguration.builder()
                .isMocked(Boolean.parseBoolean(map.get(RedisSinkConnectorConfigurationFields.REDIS_MOCKED)))
                .withHost(map.get(RedisSinkConnectorConfigurationFields.REDIS_HOST))
                .withPort(Integer.parseInt(map.get(RedisSinkConnectorConfigurationFields.REDIS_PORT)))
                .build();

        this.commandsContainer = RedisCommandsContainerBuilder.build(config);

        this.key = map.get(RedisSinkConnectorConfigurationFields.REDIS_KEY);
        this.command = RedisCommands.valueOf(map.get(RedisSinkConnectorConfigurationFields.REDIS_COMMAND));

        try {
            LOG.info("Opening Redis command container");
            this.commandsContainer.open();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        collection.forEach(this::doPut);
    }

    private void doPut(SinkRecord record) {
        String value = (String) record.value();

        switch (command) {
            case RPUSH:
                this.commandsContainer.rpush(key, value);
                break;
            case LPUSH:
                this.commandsContainer.lpush(key, value);
                break;
            default:
                throw new IllegalArgumentException("Cannot process such data type: " + command);
        }
    }

    @Override
    public void stop() {
        try {
            LOG.info("Closing Redis command container");
            this.commandsContainer.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

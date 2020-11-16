package org.isel.thesis.impads.kafka.connect.redis;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.isel.thesis.impads.kafka.connect.redis.conf.RedisConfiguration;
import org.isel.thesis.impads.kafka.connect.redis.container.RedisCommand;
import org.isel.thesis.impads.kafka.connect.redis.container.RedisCommandsContainer;
import org.isel.thesis.impads.kafka.connect.redis.container.RedisCommandsContainerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class RedisSinkTask extends SinkTask {

    private static final Logger LOG = LoggerFactory.getLogger(RedisSinkTask.class);

    private RedisConfiguration config;
    private RedisCommandsContainer commandsContainer;

    @Override
    public String version() {
        return new RedisSinkConnector().version();
    }

    @Override
    public void start(Map<String, String> map) {
        this.config = RedisConfiguration.load(map);
        this.commandsContainer = RedisCommandsContainerBuilder.build(config);
        try {
            this.commandsContainer.open();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        collection.forEach(this::doPut);
    }

    private void doPut(SinkRecord record) {
        String key = this.config.getRedisKey();
        String value = (String) record.value();
        RedisCommand redisCommand = RedisCommand.valueOf(this.config.getRedisCommand());

        switch (redisCommand) {
            case RPUSH:
                this.commandsContainer.rpush(key, value);
                break;
            case LPUSH:
                this.commandsContainer.lpush(key, value);
                break;
            case SADD:
                this.commandsContainer.sadd(key, value);
                break;
            case SET:
                this.commandsContainer.set(key, value);
                break;
            case PFADD:
                this.commandsContainer.pfadd(key, value);
                break;
            case PUBLISH:
                this.commandsContainer.publish(key, value);
                break;
            case INCRBY:
                this.commandsContainer.incrBy(key, Long.valueOf(value));
                break;
            case DECRBY:
                this.commandsContainer.decrBy(key, Long.valueOf(value));
                break;
            default:
                throw new IllegalArgumentException("Cannot process such data type: " + redisCommand);
        }
    }

    @Override
    public void stop() {
        try {
            this.commandsContainer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

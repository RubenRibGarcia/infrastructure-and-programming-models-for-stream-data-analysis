package org.apache.flink.streaming.connectors.redis.common.mapper.json;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JsonRedisMapper<T> implements RedisMapper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonRedisMapper.class);

    private final ObjectMapper mapper;
    private final RedisCommand redisCommand;
    private final String key;

    protected JsonRedisMapper(ObjectMapper mapper
            , RedisCommand redisCommand
            , String key) {
        this.mapper = mapper;
        this.redisCommand = redisCommand;
        this.key = key;
    }

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(redisCommand);
    }

    @Override
    public String getKeyFromData(T data) {
        return key;
    }

    @Override
    public String getValueFromData(T data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

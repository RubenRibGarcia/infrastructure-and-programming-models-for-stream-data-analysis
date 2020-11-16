package org.isel.thesis.impads.storm.redis.common.mapper.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.tuple.ITuple;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisCommand;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisCommandDescription;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisMapper;

public abstract class JsonTupleMapper implements RedisMapper {

    private final ObjectMapper mapper;
    private final RedisCommand command;
    private final String key;

    public JsonTupleMapper(ObjectMapper mapper
            , RedisCommand command
            , String key) {
        this.mapper = mapper;
        this.command = command;
        this.key = key;
    }

    @Override
    public String getKeyFromTuple(ITuple tuple) {
        return key;
    }

    @Override
    public String getValueFromTuple(ITuple tuple) {
        try {
            return mapper.writeValueAsString(tuple.getValueByField("value"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(command);
    }
}

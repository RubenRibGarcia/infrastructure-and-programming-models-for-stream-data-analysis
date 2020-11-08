package org.apache.flink.streaming.connectors.redis.common.mapper.json;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;

public class RPushJsonMapper<T> extends JsonRedisMapper<T> {

    public RPushJsonMapper(ObjectMapper mapper
            , String key) {

        super(mapper, RedisCommand.RPUSH, key);
    }
}

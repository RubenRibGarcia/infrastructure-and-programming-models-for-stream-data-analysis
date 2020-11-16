package org.isel.thesis.impads.storm.redis.common.mapper.json;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisCommand;

public class RPushJsonMapper extends JsonTupleMapper {

    public RPushJsonMapper(ObjectMapper mapper
            , String key) {

        super(mapper, RedisCommand.RPUSH, key);
    }
}

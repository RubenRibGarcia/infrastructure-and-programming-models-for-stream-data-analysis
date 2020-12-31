package org.isel.thesis.impads.connectors.redis.api;

import org.isel.thesis.impads.connectors.redis.container.RedisHashReadCommandsContainer;

import java.io.Serializable;

public interface RedisHashMapper<T, R> extends Serializable {

    R map(RedisHashReadCommandsContainer readCommands, RedisKeyHashField keyHashField, T t);
}

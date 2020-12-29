package org.isel.thesis.impads.connectors.redis.api;

import org.isel.thesis.impads.connectors.redis.container.RedisHashReadCommandsContainer;

import java.io.Serializable;

@FunctionalInterface
public interface RedisMapper<T, R> extends Serializable {

    R map(RedisHashReadCommandsContainer readCommands, T t);
}

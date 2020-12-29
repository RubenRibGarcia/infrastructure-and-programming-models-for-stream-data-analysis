package org.isel.thesis.impads.connectors.redis.api;

import org.isel.thesis.impads.connectors.redis.container.RedisHashReadCommandsContainer;

import java.io.Serializable;

@FunctionalInterface
public interface RedisHashLoader<T> extends Serializable {

    T load(RedisHashReadCommandsContainer readHashCommand);
}

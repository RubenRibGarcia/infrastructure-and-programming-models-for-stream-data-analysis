package org.isel.thesis.impads.storm.redis.common;

import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainer;

import java.io.Serializable;

public interface Consume<T> extends Serializable {

    void accept(RedisCommandsContainer redisCommandsContainer, T t);
}

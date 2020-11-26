package org.isel.thesis.impads.storm.redis.common;

import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainer;

import java.io.Serializable;

public interface Transform<T, R> extends Serializable {

    R apply(RedisCommandsContainer redisCommandsContainer, T t);
}

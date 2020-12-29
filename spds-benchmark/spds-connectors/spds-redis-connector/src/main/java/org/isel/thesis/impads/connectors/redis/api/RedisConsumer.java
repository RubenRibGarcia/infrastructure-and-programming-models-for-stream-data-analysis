package org.isel.thesis.impads.connectors.redis.api;

import org.isel.thesis.impads.connectors.redis.container.RedisWriteCommandsContainer;

import java.io.Serializable;

@FunctionalInterface
public interface RedisConsumer<T> extends Serializable {

    void consume(RedisWriteCommandsContainer writeCommands, T t);
}

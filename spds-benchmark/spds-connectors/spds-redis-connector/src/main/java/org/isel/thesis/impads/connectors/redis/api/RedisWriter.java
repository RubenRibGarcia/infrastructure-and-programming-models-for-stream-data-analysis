package org.isel.thesis.impads.connectors.redis.api;

import org.isel.thesis.impads.connectors.redis.container.RedisWriteCommandsContainer;

import java.io.Serializable;

public interface RedisWriter<T> extends Serializable {

    void write(RedisWriteCommandsContainer writeCommands, T t);
}

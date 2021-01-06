package org.isel.thesis.impads.connectors.redis.container;

import org.isel.thesis.impads.io.OpenCloseable;

import java.io.Serializable;

public interface RedisWriteCommandsContainer extends Serializable, OpenCloseable {

    void rpush(String listName, String value);

    void lpush(String listName, String value);
}

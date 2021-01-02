package org.isel.thesis.impads.connectors.redis.container;

import java.io.Serializable;

public interface RedisWriteCommandsContainer extends Serializable {

    void rpush(String listName, String value);

    void lpush(String listName, String value);
}

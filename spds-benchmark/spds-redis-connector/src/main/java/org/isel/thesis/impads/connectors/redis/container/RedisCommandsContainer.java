package org.isel.thesis.impads.connectors.redis.container;

import java.io.IOException;
import java.io.Serializable;

public interface RedisCommandsContainer extends RedisHashReadCommandsContainer, RedisWriteCommandsContainer, Serializable {

    void open() throws Exception;

    void close() throws IOException;
}

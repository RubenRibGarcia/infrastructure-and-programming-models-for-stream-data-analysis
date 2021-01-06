package org.isel.thesis.impads.connectors.redis.container;

import org.isel.thesis.impads.io.OpenCloseable;

import java.io.Serializable;

public interface RedisHashReadCommandsContainer extends Serializable, OpenCloseable {

    String hget(String key, String hashField);
}

package org.isel.thesis.impads.connectors.redis.container;

import java.io.Serializable;

public interface RedisHashReadCommandsContainer extends Serializable {

    String hget(String key, String hashField);
}

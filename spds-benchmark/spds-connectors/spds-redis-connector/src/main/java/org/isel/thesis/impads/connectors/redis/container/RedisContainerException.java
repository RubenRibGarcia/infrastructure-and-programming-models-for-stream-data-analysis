package org.isel.thesis.impads.connectors.redis.container;

public class RedisContainerException extends RuntimeException {

    public RedisContainerException(String message) {
        super(message);
    }

    public RedisContainerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

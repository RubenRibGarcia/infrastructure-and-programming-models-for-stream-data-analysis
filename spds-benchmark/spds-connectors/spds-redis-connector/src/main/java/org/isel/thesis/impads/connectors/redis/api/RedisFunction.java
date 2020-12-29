package org.isel.thesis.impads.connectors.redis.api;

import java.io.Serializable;

@FunctionalInterface
public interface RedisFunction<T, R> extends Serializable {

    R apply(T t);
}

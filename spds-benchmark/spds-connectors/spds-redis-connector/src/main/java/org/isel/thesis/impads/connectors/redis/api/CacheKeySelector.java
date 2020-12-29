package org.isel.thesis.impads.connectors.redis.api;

import java.io.Serializable;

@FunctionalInterface
public interface CacheKeySelector<T, I> extends Serializable {

    T getKey(I key);
}

package org.isel.thesis.impads.connectors.redis.api;

import java.io.Serializable;

public interface CacheKeySelector<I, R> extends Serializable {

    R getKey(I key);
}

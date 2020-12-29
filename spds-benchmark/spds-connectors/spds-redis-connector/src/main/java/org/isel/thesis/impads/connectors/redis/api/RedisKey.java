package org.isel.thesis.impads.connectors.redis.api;

import java.util.Objects;

public class RedisKey {

    private final String key;

    private RedisKey(final String key) {
        this.key = key;
    }

    public static RedisKey of(String key) {
        Objects.requireNonNull(key);
        return new RedisKey(key);
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RedisKey redisKey = (RedisKey) o;
        return getKey().equals(redisKey.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

}

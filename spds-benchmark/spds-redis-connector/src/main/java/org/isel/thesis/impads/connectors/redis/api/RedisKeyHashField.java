package org.isel.thesis.impads.connectors.redis.api;

import java.util.Objects;

public class RedisKeyHashField {

    private final String key;
    private final String hashField;

    private RedisKeyHashField(final String key
            , final String hashField) {
        this.key = key;
        this.hashField = hashField;
    }

    public static RedisKeyHashField of(String key, String hashField) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(hashField);
        return new RedisKeyHashField(key, hashField);
    }

    public String getKey() {
        return key;
    }

    public String getHashField() {
        return hashField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RedisKeyHashField that = (RedisKeyHashField) o;

        return getKey().equals(that.getKey()) && getHashField().equals(that.getHashField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getHashField());
    }
}

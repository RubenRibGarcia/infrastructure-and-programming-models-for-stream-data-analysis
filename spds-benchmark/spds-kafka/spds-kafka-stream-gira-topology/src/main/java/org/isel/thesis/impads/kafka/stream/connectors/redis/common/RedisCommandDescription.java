package org.isel.thesis.impads.kafka.stream.connectors.redis.common;

import java.io.Serializable;
import java.util.Objects;

public class RedisCommandDescription implements Serializable {

    private static final long serialVersionUID = 1L;

    private RedisCommand redisCommand;

    private String additionalKey;

    private Integer additionalTTL;

    public RedisCommandDescription(RedisCommand redisCommand, String additionalKey, Integer additionalTTL) {
        Objects.requireNonNull(redisCommand, "Redis command type can not be null");
        this.redisCommand = redisCommand;
        this.additionalKey = additionalKey;
        this.additionalTTL = additionalTTL;

        if (redisCommand.getRedisDataType() == RedisDataType.HASH ||
            redisCommand.getRedisDataType() == RedisDataType.SORTED_SET) {
            if (additionalKey == null) {
                throw new IllegalArgumentException("Hash and Sorted Set should have additional key");
            }
        }

        if (redisCommand.equals(RedisCommand.SETEX)) {
            if (additionalTTL == null) {
                throw new IllegalArgumentException("SETEX command should have time to live (TTL)");
            }
        }

        if (redisCommand.equals(RedisCommand.INCRBY_EX)) {
            if (additionalTTL == null) {
                throw new IllegalArgumentException("INCRBY_EX command should have time to live (TTL)");
            }
        }

        if (redisCommand.equals(RedisCommand.DESCRBY_EX)) {
            if (additionalTTL == null) {
                throw new IllegalArgumentException("INCRBY_EX command should have time to live (TTL)");
            }
        }
    }

    public RedisCommandDescription(RedisCommand redisCommand, String additionalKey) {
        this(redisCommand, additionalKey, null);
    }

    public RedisCommandDescription(RedisCommand redisCommand, Integer additionalTTL) {
        this(redisCommand, null, additionalTTL);
    }

    public RedisCommandDescription(RedisCommand redisCommand) {
        this(redisCommand, null, null);
    }

    public RedisCommand getCommand() {
        return redisCommand;
    }

    public String getAdditionalKey() { return additionalKey; }

    public Integer getAdditionalTTL() { return additionalTTL; }
}

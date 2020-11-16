package org.isel.thesis.impads.storm.redis.common.mapper;

import org.apache.storm.tuple.ITuple;

import java.io.Serializable;
import java.util.Optional;

/**
 * RedisMapper is for defining data type for querying / storing from / to Redis.
 */
public interface RedisMapper extends Serializable {
    /**
     * Returns descriptor which defines data type.
     *
     * @return data type descriptor
     */
    RedisCommandDescription getCommandDescription();

    /**
     * Extracts key from data.
     *
     * @param data source data
     * @return key
     */
    String getKeyFromTuple(ITuple data);

    /**
     * Extracts value from data.
     *
     * @param data source data
     * @return value
     */
    String getValueFromTuple(ITuple data);

    /**
     * Extracts the additional key from data as an {@link Optional <String>}.
     * The default implementation returns an empty Optional.
     *
     * @param data
     * @return Optional
     */
    default Optional<String> getAdditionalKey(ITuple data) {
        return Optional.empty();
    }

    default Optional<Integer> getAdditionalTTL(ITuple data) {
        return Optional.empty();
    }
}

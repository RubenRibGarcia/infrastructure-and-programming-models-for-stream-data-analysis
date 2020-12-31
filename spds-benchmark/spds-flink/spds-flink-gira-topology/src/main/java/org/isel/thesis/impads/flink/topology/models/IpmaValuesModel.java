package org.isel.thesis.impads.flink.topology.models;

import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableSet;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.isel.thesis.impads.connectors.redis.container.RedisHashReadCommandsContainer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IpmaValuesModel implements Serializable {

    private static final Set<String> LIST_OF_KEYS = ImmutableSet.<String>builder().add("humidade_media_ar"
            , "precipitacao_total"
            , "radiacao_total"
            , "temperatura_media_ar"
            , "vento_intensidade_media"
            , "vento_rumo_media").build();

    private static final long serialVersionUID = 1L;

    private final Map<String, String> ipmaSensoresValues;

    private IpmaValuesModel(Map<String, String> ipmaSensoresValues) {
        this.ipmaSensoresValues = ipmaSensoresValues;
    }

    public static IpmaValuesModel fetchAndAddFromRedis(String hashField, RedisHashReadCommandsContainer container) {
        Map<String, String> ipmaSensoresValues = new HashMap<>();
        LIST_OF_KEYS.forEach(key ->
                ipmaSensoresValues.putIfAbsent(key, container.hget(key, hashField)));

        return new IpmaValuesModel(ipmaSensoresValues);
    }

    @JsonProperty("ipma_sensores_values")
    public Map<String, String> getIpmaSensoresValues() {
        return ipmaSensoresValues;
    }
}

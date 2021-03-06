package org.isel.thesis.impads.kafka.stream.topology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.isel.thesis.impads.connectors.redis.container.RedisHashReadCommandsContainer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IpmaValuesModel implements Serializable {

    private static final Set<String> LIST_OF_KEYS = new HashSet<>(){{
       add("humidade_media_ar");
       add("precipitacao_total");
       add("radiacao_total");
       add("temperatura_media_ar");
       add("vento_intensidade_media");
       add("vento_rumo_media");
    }};

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

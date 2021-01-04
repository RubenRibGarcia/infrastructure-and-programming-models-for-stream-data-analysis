package org.isel.thesis.impads.kafka.stream.topology;

import com.typesafe.config.Config;
import org.isel.thesis.impads.connectors.redis.common.RedisConfiguration;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationFields;
import org.isel.thesis.impads.kafka.stream.topology.GiraTravelsTopologyConfiguration.GiraTravelsTopologyConfigurationFields;
import org.isel.thesis.impads.kafka.stream.topology.phases.Phases;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfigurationFields;
import org.isel.thesis.impads.metrics.collector.api.MetricsStatsDAgent;

public final class ConfigurationContainer {

    private final GiraTravelsTopologyConfiguration topologyConfiguration;
    private final RedisConfiguration redisConfiguration;
    private final MetricsCollectorConfiguration metricsCollectorConfiguration;

    private ConfigurationContainer(GiraTravelsTopologyConfiguration topologyConfiguration
            , RedisConfiguration redisConfiguration
            , MetricsCollectorConfiguration metricsCollectorConfiguration) {
        this.topologyConfiguration = topologyConfiguration;
        this.redisConfiguration = redisConfiguration;
        this.metricsCollectorConfiguration = metricsCollectorConfiguration;
    }

    public static ConfigurationContainer setup(Config config) {
        return new ConfigurationContainer(doInitializeGiraTravelsTopologyConfiguration(config)
                , doInitializeRedisConfiguration(config)
                , doInitializeMetricsCollectorConfiguration(config));
    }

    private static GiraTravelsTopologyConfiguration doInitializeGiraTravelsTopologyConfiguration(Config config) {
        return GiraTravelsTopologyConfiguration.builder()
                .withParallelism(config.getInt(GiraTravelsTopologyConfigurationFields.TOPOLOGY_PARALLELISM))
                .withUntilPhase(config.getEnum(Phases.class, GiraTravelsTopologyConfigurationFields.TOPOLOGY_UNTIL_PHASE))
                .build();
    }

    private static RedisConfiguration doInitializeRedisConfiguration(Config config) {
        return RedisConfiguration.builder()
                .isMocked(config.getBoolean(RedisConfigurationFields.REDIS_MOCKED))
                .withHost(config.getString(RedisConfigurationFields.REDIS_HOST))
                .withPort(config.getInt(RedisConfigurationFields.REDIS_PORT))
                .build();
    }

    private static MetricsCollectorConfiguration doInitializeMetricsCollectorConfiguration(Config config) {
        return new MetricsCollectorConfiguration(config.getEnum(MetricsStatsDAgent.class, MetricsCollectorConfigurationFields.METRICS_STATSD_AGENT)
                , config.getString(MetricsCollectorConfigurationFields.METRICS_STATSD_HOST)
                , config.getInt(MetricsCollectorConfigurationFields.METRICS_STATSD_PORT));
    }

    public GiraTravelsTopologyConfiguration getTopologyConfiguration() {
        return topologyConfiguration;
    }

    public RedisConfiguration getRedisConfiguration() {
        return redisConfiguration;
    }

    public MetricsCollectorConfiguration getMetricsCollectorConfiguration() {
        return metricsCollectorConfiguration;
    }
}

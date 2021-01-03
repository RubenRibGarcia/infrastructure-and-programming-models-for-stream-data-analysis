package org.isel.thesis.impads.flink.topology;

import com.typesafe.config.Config;
import org.isel.thesis.impads.connectors.redis.common.RedisConfiguration;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationBase;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationFields;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.RabbitMQConfiguration;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.RabbitMQConfigurationFields;
import org.isel.thesis.impads.flink.topology.GiraTravelsTopologyConfiguration.GiraTravelsTopologyConfigurationFields;
import org.isel.thesis.impads.flink.topology.phases.Phases;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfigurationFields;
import org.isel.thesis.impads.metrics.collector.api.MetricsStatsDAgent;

import java.io.Serializable;

public final class ConfigurationContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    private final GiraTravelsTopologyConfiguration topologyConfiguration;
    private final RabbitMQConfiguration rabbitMQConfiguration;
    private final RedisConfigurationBase redisConfiguration;
    private final MetricsCollectorConfiguration metricsCollectorConfiguration;

    private ConfigurationContainer(GiraTravelsTopologyConfiguration topologyConfiguration
            , RabbitMQConfiguration rabbitMQConfiguration
            , RedisConfigurationBase redisConfiguration
            , MetricsCollectorConfiguration metricsCollectorConfiguration) {
        this.topologyConfiguration = topologyConfiguration;
        this.rabbitMQConfiguration = rabbitMQConfiguration;
        this.redisConfiguration = redisConfiguration;
        this.metricsCollectorConfiguration = metricsCollectorConfiguration;
    }

    public static ConfigurationContainer setup(Config config) {
        return new ConfigurationContainer( doInitializeGiraTravelsTopologyConfiguration(config)
                , doInitializeRabbitMQConfiguration(config)
                , doInitializeRedisConfiguration(config)
                , doInitializeMetricsCollectorConfiguration(config));
    }

    private static GiraTravelsTopologyConfiguration doInitializeGiraTravelsTopologyConfiguration(Config config) {
        return GiraTravelsTopologyConfiguration.builder()
                .withParallelism(config.getInt(GiraTravelsTopologyConfigurationFields.TOPOLOGY_PARALLELISM))
                .withUntilPhase(Phases.valueOf(config.getString(GiraTravelsTopologyConfigurationFields.TOPOLOGY_UNTIL_PHASE)))
                .build();
    }

    private static RabbitMQConfiguration doInitializeRabbitMQConfiguration(Config config) {
        return RabbitMQConfiguration.Builder.builder()
                .withHost(config.getString(RabbitMQConfigurationFields.RABBITMQ_HOST))
                .withPort(config.getInt(RabbitMQConfigurationFields.RABBITMQ_PORT))
                .withVirtualHost(config.getString(RabbitMQConfigurationFields.RABBITMQ_VIRTUAL_HOST))
                .withUsername(config.getString(RabbitMQConfigurationFields.RABBITMQ_USERNAME))
                .withPassword(config.getString(RabbitMQConfigurationFields.RABBITMQ_PASSWORD))
                .build();
    }

    private static RedisConfigurationBase doInitializeRedisConfiguration(Config config) {
        return RedisConfiguration.builder()
                .withHost(config.getString(RedisConfigurationFields.REDIS_HOST))
                .withPort(config.getInt(RedisConfigurationFields.REDIS_PORT))
                .isMocked(config.getBoolean(RedisConfigurationFields.REDIS_MOCKED))
                .build();
    }

    private static MetricsCollectorConfiguration doInitializeMetricsCollectorConfiguration(Config config) {
        return new MetricsCollectorConfiguration(MetricsStatsDAgent.valueOf(config.getString(MetricsCollectorConfigurationFields.METRICS_STATSD_AGENT))
                , config.getString(MetricsCollectorConfigurationFields.METRICS_STATSD_HOST)
                , config.getInt(MetricsCollectorConfigurationFields.METRICS_STATSD_PORT));
    }

    public GiraTravelsTopologyConfiguration getTopologyConfiguration() {
        return topologyConfiguration;
    }

    public RabbitMQConfiguration getRabbitMQConfiguration() {
        return rabbitMQConfiguration;
    }

    public RedisConfigurationBase getRedisConfiguration() {
        return redisConfiguration;
    }

    public MetricsCollectorConfiguration getMetricsCollectorConfiguration() {
        return metricsCollectorConfiguration;
    }
}

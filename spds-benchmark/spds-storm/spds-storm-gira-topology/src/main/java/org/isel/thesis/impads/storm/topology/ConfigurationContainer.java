package org.isel.thesis.impads.storm.topology;

import com.typesafe.config.Config;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfigurationFields;
import org.isel.thesis.impads.metrics.collector.api.MetricsStatsDAgent;
import org.isel.thesis.impads.storm.redis.common.RedisConfigurationFields;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.RabbitMQConfigurationFields;
import org.isel.thesis.impads.storm.topology.GiraTravelsTopologyConfiguration.GiraTravelsTopologyConfigurationFields;
import org.isel.thesis.impads.storm.topology.phases.Phases;

import java.io.Serializable;

public final class ConfigurationContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    private final GiraTravelsTopologyConfiguration topologyConfiguration;
    private final RabbitMQConfiguration rabbitMQConfiguration;
    private final JedisPoolConfig redisConfiguration;
    private final MetricsCollectorConfiguration metricsCollectorConfiguration;

    private ConfigurationContainer(GiraTravelsTopologyConfiguration topologyConfiguration
            , RabbitMQConfiguration rabbitMQConfiguration
            , JedisPoolConfig redisConfiguration
            , MetricsCollectorConfiguration metricsCollectorConfiguration) {
        this.topologyConfiguration = topologyConfiguration;
        this.rabbitMQConfiguration = rabbitMQConfiguration;
        this.redisConfiguration = redisConfiguration;
        this.metricsCollectorConfiguration = metricsCollectorConfiguration;
    }

    public static ConfigurationContainer setup(Config config) {
        return new ConfigurationContainer(doInitializeGiraTravelsTopologyConfiguration(config)
                , doInitializeRabbitMQConfiguration(config)
                , doInitializeRedisConfiguration(config)
                , doInitializeMetricsCollectorConfiguration(config));
    }

    private static GiraTravelsTopologyConfiguration doInitializeGiraTravelsTopologyConfiguration(Config config) {
        return GiraTravelsTopologyConfiguration.builder()
                .withParallelism(config.getInt(GiraTravelsTopologyConfigurationFields.TOPOLOGY_PARALLELISM))
                .withUntilPhase(config.getEnum(Phases.class, GiraTravelsTopologyConfigurationFields.TOPOLOGY_UNTIL_PHASE))
                .build();
    }

    private static RabbitMQConfiguration doInitializeRabbitMQConfiguration(Config config) {
        return RabbitMQConfiguration.Builder.builder()
                .withHost(config.getString(RabbitMQConfigurationFields.RABBITMQ_HOST))
                .withPort(config.getInt(RabbitMQConfigurationFields.RABBITMQ_PORT))
                .withUsername(config.getString(RabbitMQConfigurationFields.RABBITMQ_USERNAME))
                .withPassword(config.getString(RabbitMQConfigurationFields.RABBITMQ_PASSWORD))
                .build();
    }

    private static JedisPoolConfig doInitializeRedisConfiguration(Config config) {
        return new JedisPoolConfig.Builder()
                .setHost(config.getString(RedisConfigurationFields.REDIS_HOST))
                .setPort(config.getInt(RedisConfigurationFields.REDIS_PORT))
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

    public RabbitMQConfiguration getRabbitMQConfiguration() {
        return rabbitMQConfiguration;
    }

    public JedisPoolConfig getRedisConfiguration() {
        return redisConfiguration;
    }

    public MetricsCollectorConfiguration getMetricsCollectorConfiguration() {
        return metricsCollectorConfiguration;
    }
}

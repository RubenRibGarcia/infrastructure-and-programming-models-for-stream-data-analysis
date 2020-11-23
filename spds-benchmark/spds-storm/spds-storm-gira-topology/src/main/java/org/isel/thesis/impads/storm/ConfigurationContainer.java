package org.isel.thesis.impads.storm;

import com.typesafe.config.Config;
import org.apache.storm.streams.RedisConfigurationFields;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfigurationFields;
import org.isel.thesis.impads.metrics.collector.api.MetricsStatsDAgent;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.RabbitMQConfigurationFields;

public final class ConfigurationContainer {

    private final RabbitMQConfiguration rabbitMQConfiguration;
    private final JedisPoolConfig redisConfiguration;
    private final MetricsCollectorConfiguration metricsCollectorConfiguration;

    private ConfigurationContainer(RabbitMQConfiguration rabbitMQConfiguration
            , JedisPoolConfig redisConfiguration
            , MetricsCollectorConfiguration metricsCollectorConfiguration) {
        this.rabbitMQConfiguration = rabbitMQConfiguration;
        this.redisConfiguration = redisConfiguration;
        this.metricsCollectorConfiguration = metricsCollectorConfiguration;
    }

    public static ConfigurationContainer setup(Config config) {
        return new ConfigurationContainer(doInitializeRabbitMQConfiguration(config)
                , doInitializeRedisConfiguration(config)
                , doInitializeMetricsCollectorConfiguration(config));
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
                .setMinIdle(config.getInt(RedisConfigurationFields.REDIS_MIN_IDLE))
                .setMaxIdle(config.getInt(RedisConfigurationFields.REDIS_MAX_IDLE))
                .setMaxTotal(config.getInt(RedisConfigurationFields.REDIS_MAX_TOTAL))
                .build();
    }

    private static MetricsCollectorConfiguration doInitializeMetricsCollectorConfiguration(Config config) {
        return new MetricsCollectorConfiguration(config.getEnum(MetricsStatsDAgent.class, MetricsCollectorConfigurationFields.METRICS_STATSD_AGENT)
                , config.getString(MetricsCollectorConfigurationFields.METRICS_STATSD_HOST)
                , config.getInt(MetricsCollectorConfigurationFields.METRICS_STATSD_PORT));
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
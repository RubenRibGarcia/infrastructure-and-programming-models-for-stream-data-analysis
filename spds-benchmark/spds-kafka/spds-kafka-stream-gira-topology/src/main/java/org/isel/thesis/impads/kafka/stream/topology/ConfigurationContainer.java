package org.isel.thesis.impads.kafka.stream.topology;

import com.typesafe.config.Config;
import org.isel.thesis.impads.kafka.stream.connectors.redis.RedisConfigurationFields;
import org.isel.thesis.impads.kafka.stream.connectors.redis.common.config.JedisPoolConfig;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfigurationFields;
import org.isel.thesis.impads.metrics.collector.api.MetricsStatsDAgent;

public final class ConfigurationContainer {

    private final JedisPoolConfig redisConfiguration;
    private final MetricsCollectorConfiguration metricsCollectorConfiguration;

    private ConfigurationContainer(JedisPoolConfig redisConfiguration
            , MetricsCollectorConfiguration metricsCollectorConfiguration) {
        this.redisConfiguration = redisConfiguration;
        this.metricsCollectorConfiguration = metricsCollectorConfiguration;
    }

    public static ConfigurationContainer setup(Config config) {
        return new ConfigurationContainer(doInitializeRedisConfiguration(config)
                , doInitializeMetricsCollectorConfiguration(config));
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

    public JedisPoolConfig getRedisConfiguration() {
        return redisConfiguration;
    }

    public MetricsCollectorConfiguration getMetricsCollectorConfiguration() {
        return metricsCollectorConfiguration;
    }
}

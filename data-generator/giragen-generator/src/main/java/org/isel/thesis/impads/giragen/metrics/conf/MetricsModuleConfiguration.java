package org.isel.thesis.impads.giragen.metrics.conf;

import com.typesafe.config.Config;
import org.isel.thesis.impads.giragen.metrics.api.MetricStatsDAgent;

import javax.inject.Inject;

public class MetricsModuleConfiguration {

    private static final String METRICS_STATSD_AGENT = "metrics.statsd.agent";
    private static final String METRICS_STATSD_HOST = "metrics.statsd.host";
    private static final String METRICS_STATSD_PORT = "metrics.statsd.port";

    private final Config config;

    @Inject
    public MetricsModuleConfiguration(Config config) {
        this.config = config;
    }

    public MetricStatsDAgent getMetricStatsDAgent() {
        return MetricStatsDAgent.valueOf(config.getString(METRICS_STATSD_AGENT));
    }

    public String getMetricStatsDHost() {
        return config.getString(METRICS_STATSD_HOST);
    }

    public int getMetricStatsDPort() {
        return config.getInt(METRICS_STATSD_PORT);
    }
}

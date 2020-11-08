package org.isel.thesis.impads.metrics.collector;

import org.isel.thesis.impads.metrics.collector.api.MetricsStatsDAgent;

public final class MetricsCollectorConfiguration {

    private final MetricsStatsDAgent metricsStatsDAgent;
    private final String metricsStatsDHost;
    private final int metricsStatsDPort;

    public MetricsCollectorConfiguration(MetricsStatsDAgent metricsStatsDAgent
            , String metricsStatsDHost
            , int metricsStatsDPort) {
        this.metricsStatsDAgent = metricsStatsDAgent;
        this.metricsStatsDHost = metricsStatsDHost;
        this.metricsStatsDPort = metricsStatsDPort;
    }

    public MetricsStatsDAgent getMetricsStatsDAgent() {
        return metricsStatsDAgent;
    }

    public String getMetricStatsDHost() {
        return metricsStatsDHost;
    }

    public int getMetricStatsDPort() {
        return metricsStatsDPort;
    }
}

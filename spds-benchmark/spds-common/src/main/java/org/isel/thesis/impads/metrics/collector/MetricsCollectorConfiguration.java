package org.isel.thesis.impads.metrics.collector;

import org.isel.thesis.impads.metrics.collector.api.MetricsStatsDAgent;

import java.io.Serializable;

public final class MetricsCollectorConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

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

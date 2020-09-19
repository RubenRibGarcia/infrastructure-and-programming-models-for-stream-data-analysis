package org.isel.thesis.impads.metrics.collector.api;

public interface IMetricsCollectorConfiguration {

    String METRICS_STATSD_AGENT = "metrics.statsd.agent";
    String METRICS_STATSD_HOST = "metrics.statsd.host";
    String METRICS_STATSD_PORT = "metrics.statsd.port";

    MetricStatsDAgent getMetricStatsDAgent();

    String getMetricStatsDHost();

    int getMetricStatsDPort();
}

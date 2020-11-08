package org.isel.thesis.impads.metrics.collector.statsd;

import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;

import javax.inject.Inject;

public class TelegrafStatsD implements StatsdConfig {

    private final MetricsCollectorConfiguration config;

    @Inject
    public TelegrafStatsD(MetricsCollectorConfiguration config) {
        this.config = config;
    }

    @Override
    public String get(String s) {
        return null;
    }

    @Override
    public String prefix() {
        return "";
    }

    @Override
    public StatsdFlavor flavor() {
        return StatsdFlavor.TELEGRAF;
    }

    @Override
    public String host() {
        return config.getMetricStatsDHost();
    }

    @Override
    public int port() {
        return config.getMetricStatsDPort();
    }
}

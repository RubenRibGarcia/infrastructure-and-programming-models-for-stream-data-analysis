package org.isel.thesis.impads.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.isel.thesis.impads.metrics.collector.Metrics;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.api.IMetrics;
import org.isel.thesis.impads.metrics.collector.statsd.TelegrafStatsD;

public final class FactoryMetrics {

    public static IMetrics newMetrics(MetricsCollectorConfiguration collectorConfiguration) {
        return provideMetrics(collectorConfiguration);
    }

    private static IMetrics provideMetrics(MetricsCollectorConfiguration collectorConfiguration) {
        final IMetrics metrics;
        switch (collectorConfiguration.getMetricsStatsDAgent()) {
            case TELEGRAF:
                metrics = new Metrics(new StatsdMeterRegistry(new TelegrafStatsD(collectorConfiguration), Clock.SYSTEM));
                break;
            case NONE:
            default:
                metrics = null;
                break;
        }

        return metrics;
    }
}

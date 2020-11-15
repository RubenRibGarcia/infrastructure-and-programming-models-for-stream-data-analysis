package org.isel.thesis.impads.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Timer;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.isel.thesis.impads.metrics.collector.Metrics;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.api.IMetrics;
import org.isel.thesis.impads.metrics.collector.statsd.TelegrafStatsD;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ObservableMetricsCollector {

    private static final String EVENT_TIME_LATENCY_METRIC_SUFFIX = ".event_time.latency";
    private static final String PROCESSING_TIME_LATENCY_METRIC_SUFFIX = ".processing_time.latency";

    private final MetricsCollectorConfiguration config;
    private final SPDS spds;

    private Timer eventLatencyTimer;
    private Timer processingLatencyTimer;

    public ObservableMetricsCollector(MetricsCollectorConfiguration config
            , SPDS spds) {
        this.config = config;
        this.spds = spds;
        this.initMetricsCollector();
    }

    private void initMetricsCollector() {
        IMetrics metrics = provideMetrics(config);

        if (metrics != null && spds != null) {
            this.eventLatencyTimer = metrics.registerTimer(spds.getName().concat(EVENT_TIME_LATENCY_METRIC_SUFFIX));
            this.processingLatencyTimer = metrics.registerTimer(spds.getName().concat(PROCESSING_TIME_LATENCY_METRIC_SUFFIX));
        }
    }

    public void collect(Observable<?> data) {
        if (this.eventLatencyTimer != null && this.processingLatencyTimer != null) {
            long nowTimestamp = Instant.now().toEpochMilli();
            this.eventLatencyTimer.record(ObservableUtils.eventTimeLatencyInMillis(nowTimestamp, data.getEventTimestamp())
                    , TimeUnit.MILLISECONDS);
            this.processingLatencyTimer.record(ObservableUtils.processingTimeLatencyInMillis(nowTimestamp, data.getIngestionTimestamp())
                    , TimeUnit.MILLISECONDS);
        }
    }

    private IMetrics provideMetrics(MetricsCollectorConfiguration collectorConfiguration) {
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

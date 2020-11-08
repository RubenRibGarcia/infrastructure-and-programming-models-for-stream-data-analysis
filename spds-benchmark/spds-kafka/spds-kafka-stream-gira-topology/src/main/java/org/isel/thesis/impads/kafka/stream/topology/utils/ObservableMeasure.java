package org.isel.thesis.impads.kafka.stream.topology.utils;

import com.typesafe.config.Config;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Timer;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.isel.thesis.impads.metrics.ObservableUtils;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.metrics.collector.Metrics;
import org.isel.thesis.impads.metrics.collector.api.IMetrics;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.api.MetricsStatsDAgent;
import org.isel.thesis.impads.metrics.collector.statsd.TelegrafStatsD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public final class ObservableMeasure {

    private static final Logger logger = LoggerFactory.getLogger(ObservableMeasure.class);

    private Timer eventLatencyTimer;
    private Timer processingLatencyTimer;

    public ObservableMeasure(Config config) {
        initMetrics(config);
    }

    private IMetrics initMetrics(Config config) {

        IMetrics metrics = configureMetrics(new MetricsCollectorConfiguration() {
            @Override
            public MetricsStatsDAgent getMetricStatsDAgent() {
                return config.getEnum(MetricsStatsDAgent.class, METRICS_STATSD_AGENT);
            }

            @Override
            public String getMetricStatsDHost() {
                return config.getString(METRICS_STATSD_HOST);
            }

            @Override
            public int getMetricStatsDPort() {
                return config.getInt(METRICS_STATSD_PORT);
            }
        });


        if (metrics != null) {
            this.eventLatencyTimer = metrics.registerTimer("kafka_stream.event_time.latency");
            this.processingLatencyTimer = metrics.registerTimer("kafka_stream.processing_time.latency");
        }

        return metrics;
    }

    private IMetrics configureMetrics(MetricsCollectorConfiguration collectorConfiguration) {
        final IMetrics metrics;
        switch (collectorConfiguration.getMetricStatsDAgent()) {
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

    public void measure(Observable<?> observable) {
        if (this.eventLatencyTimer != null && this.processingLatencyTimer != null) {
            long nowTimestamp = Instant.now().toEpochMilli();
            this.eventLatencyTimer.record(ObservableUtils.eventTimeLatencyInMillis(nowTimestamp, observable.getEventTimestamp()), TimeUnit.MILLISECONDS);
            this.processingLatencyTimer.record(ObservableUtils.processingTimeLatencyInMillis(nowTimestamp, observable.getIngestionTimestamp()), TimeUnit.MILLISECONDS);
        }
    }
}

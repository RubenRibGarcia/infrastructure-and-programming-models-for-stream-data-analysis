package org.isel.thesis.impads.flink.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Timer;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;
import org.isel.thesis.impads.metrics.ObservableUtils;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.metrics.collector.Metrics;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.api.IMetrics;
import org.isel.thesis.impads.metrics.collector.statsd.TelegrafStatsD;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ObservableFilterFunction<T extends Observable<?>> extends RichFilterFunction<T> {

    private final MetricsCollectorConfiguration config;
    private final RichFilterFunction<T> filterFunction;

    private transient Timer eventLatencyTimer;
    private transient Timer processingLatencyTimer;

    private ObservableFilterFunction(MetricsCollectorConfiguration config
            , RichFilterFunction<T> filterFunction) {
        this.config = config;
        this.filterFunction = filterFunction;
    }

    public static <T extends Observable<?>> ObservableFilterFunction<T> observe(MetricsCollectorConfiguration config
            , RichFilterFunction<T> filterFunction) {
        return new ObservableFilterFunction<>(config, filterFunction);
    }

    public static <T extends Observable<?>> ObservableFilterFunction<T> observe(MetricsCollectorConfiguration config
            , FilterFunction<T> filterFunction) {
        return new ObservableFilterFunction<>(config, new RichFilterFunction<T>() {
            @Override
            public boolean filter(T t) throws Exception {
                return filterFunction.filter(t);
            }
        });
    }

    @Override
    public boolean filter(T t) throws Exception {
        boolean retain = filterFunction.filter(t);

        if (!retain) {
            registerTimers(t);
        }

        return retain;
    }

    private void registerTimers(T value) {
        if (this.eventLatencyTimer != null && this.processingLatencyTimer != null) {
            long nowTimestamp = Instant.now().toEpochMilli();
            this.eventLatencyTimer.record(ObservableUtils.eventTimeLatencyInMillis(nowTimestamp, value.getEventTimestamp()), TimeUnit.MILLISECONDS);
            this.processingLatencyTimer.record(ObservableUtils.processingTimeLatencyInMillis(nowTimestamp, value.getIngestionTimestamp()), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        IMetrics metrics = provideMetrics(config);

        if (metrics != null) {
            this.eventLatencyTimer = metrics.registerTimer("flink.event_time.latency");
            this.processingLatencyTimer = metrics.registerTimer("flink.processing_time.latency");
        }

        if (filterFunction != null) {
            filterFunction.open(parameters);
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

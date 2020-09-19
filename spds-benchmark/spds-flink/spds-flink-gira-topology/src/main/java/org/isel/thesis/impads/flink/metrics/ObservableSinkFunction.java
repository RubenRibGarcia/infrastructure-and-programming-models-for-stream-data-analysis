package org.isel.thesis.impads.flink.metrics;

import com.typesafe.config.Config;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Timer;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.isel.thesis.impads.metrics.ObservableUtils;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.metrics.collector.Metrics;
import org.isel.thesis.impads.metrics.collector.api.IMetrics;
import org.isel.thesis.impads.metrics.collector.api.IMetricsCollectorConfiguration;
import org.isel.thesis.impads.metrics.collector.api.MetricStatsDAgent;
import org.isel.thesis.impads.metrics.collector.statsd.TelegrafStatsD;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ObservableSinkFunction<T extends Observable<?>> extends RichSinkFunction<T> {

    private final Config config;
    private final RichSinkFunction<T> sinkFunction;

    private transient Timer eventLatencyTimer;
    private transient Timer processingLatencyTimer;

    private ObservableSinkFunction(final Config config
            , final RichSinkFunction<T> sinkFunction) {
        this.config = config;
        this.sinkFunction = sinkFunction;
        this.eventLatencyTimer = null;
        this.processingLatencyTimer = null;
    }

    public static <T extends Observable<?>> ObservableSinkFunction<T> observe(Config config) {
        return observe(config, null);
    }

    public static <T extends Observable<?>> ObservableSinkFunction<T> observe(Config config
            , RichSinkFunction<T> sinkFunction) {
        return new ObservableSinkFunction<>(config
                , sinkFunction);
    }

    @Override
    public void invoke(T value, Context context) throws Exception {

        registerTimers(value);

        if (sinkFunction != null) {
            sinkFunction.invoke(value, context);
        }
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
        IMetrics metrics = provideMetrics(new IMetricsCollectorConfiguration() {
            @Override
            public MetricStatsDAgent getMetricStatsDAgent() {
                return config.getEnum(MetricStatsDAgent.class, METRICS_STATSD_AGENT);
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
            this.eventLatencyTimer = metrics.registerTimer("flink.event_time.latency");
            this.processingLatencyTimer = metrics.registerTimer("flink.processing_time.latency");
        }

        if (sinkFunction != null) {
            sinkFunction.open(parameters);
        }
    }

    private IMetrics provideMetrics(IMetricsCollectorConfiguration collectorConfiguration) {
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
}

package org.isel.thesis.impads.storm.metrics;

import com.typesafe.config.Config;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Timer;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.isel.thesis.impads.metrics.collector.MetricsCollectorConfigurationFields.METRICS_STATSD_AGENT;
import static org.isel.thesis.impads.metrics.collector.MetricsCollectorConfigurationFields.METRICS_STATSD_HOST;
import static org.isel.thesis.impads.metrics.collector.MetricsCollectorConfigurationFields.METRICS_STATSD_PORT;

public class ObservableBolt implements IRichBolt {

    private static final Logger LOG = LoggerFactory.getLogger(ObservableBolt.class);

    private final Config config;
    private final IRichBolt richBolt;

    private transient Timer eventLatencyTimer;
    private transient Timer processingLatencyTimer;

    private ObservableBolt(final Config config
            , final IRichBolt richBolt) {
        this.config = config;
        this.richBolt = richBolt;
        this.eventLatencyTimer = null;
        this.processingLatencyTimer = null;
    }

    public static ObservableBolt observe(Config config) {
        return observe(config, null);
    }

    public static ObservableBolt observe(Config config
            , IRichBolt richBolt) {
        return new ObservableBolt(config
                , richBolt);
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {

        IMetrics metrics = provideMetrics(new MetricsCollectorConfiguration() {
            @Override
            public MetricsStatsDAgent getMetricsStatsDAgent() {
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
            this.eventLatencyTimer = metrics.registerTimer("storm.event_time.latency");
            this.processingLatencyTimer = metrics.registerTimer("storm.processing_time.latency");
        }

        if (richBolt != null) {
            richBolt.prepare(topoConf, context, collector);
        }
    }

    private IMetrics provideMetrics(MetricsCollectorConfiguration collectorConfiguration) {
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

    @Override
    public void execute(Tuple input) {
        LOG.info("Sending latency metrics");

        registerTimers((Observable<?>) input.getValueByField("value"));

        if (richBolt != null) {
            richBolt.execute(input);
        }
    }

    private void registerTimers(Observable<?> input) {
        if (this.eventLatencyTimer != null && this.processingLatencyTimer != null) {
            long nowTimestamp = Instant.now().toEpochMilli();
            this.eventLatencyTimer.record(ObservableUtils.eventTimeLatencyInMillis(nowTimestamp, input.getEventTimestamp()), TimeUnit.MILLISECONDS);
            this.processingLatencyTimer.record(ObservableUtils.processingTimeLatencyInMillis(nowTimestamp, input.getIngestionTimestamp()), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}

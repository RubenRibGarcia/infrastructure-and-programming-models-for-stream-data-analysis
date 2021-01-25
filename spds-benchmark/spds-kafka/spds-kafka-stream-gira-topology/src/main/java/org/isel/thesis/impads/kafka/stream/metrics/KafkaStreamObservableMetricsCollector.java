package org.isel.thesis.impads.kafka.stream.metrics;

import org.isel.thesis.impads.metrics.ObservableMetricsCollector;
import org.isel.thesis.impads.metrics.SPDS;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;

public class KafkaStreamObservableMetricsCollector
        extends ObservableMetricsCollector {

    public KafkaStreamObservableMetricsCollector(MetricsCollectorConfiguration config, String... tags) {
        super(config, SPDS.KAFKA_STREAM, tags);
    }
}

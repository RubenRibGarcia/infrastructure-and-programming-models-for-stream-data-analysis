package org.isel.thesis.impads.flink.metrics;

import org.isel.thesis.impads.metrics.ObservableMetricsCollector;
import org.isel.thesis.impads.metrics.SPDS;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;

public class FlinkObservableMetricsCollector
        extends ObservableMetricsCollector {

    private static final long serialVersionUID = 1L;

    public FlinkObservableMetricsCollector(MetricsCollectorConfiguration config, String... tags) {
        super(config, SPDS.FLINK, tags);
    }
}

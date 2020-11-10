package org.isel.thesis.impads.flink.metrics;

import org.isel.thesis.impads.metrics.ObservableMetricsCollector;
import org.isel.thesis.impads.metrics.SPDS;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;

public class FlinkObservableMetricsCollector
        extends ObservableMetricsCollector {


    public FlinkObservableMetricsCollector(MetricsCollectorConfiguration config) {
        super(config, SPDS.FLINK);
    }
}

package org.isel.thesis.impads.storm.metrics;

import org.isel.thesis.impads.metrics.ObservableMetricsCollector;
import org.isel.thesis.impads.metrics.SPDS;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;

public class StormObservableMetricsCollector extends ObservableMetricsCollector {

    public StormObservableMetricsCollector(MetricsCollectorConfiguration config) {
        super(config, SPDS.STORM);
    }
}

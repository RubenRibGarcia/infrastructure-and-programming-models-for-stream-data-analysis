package org.isel.thesis.impads.giragen.metrics.statsd;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.isel.thesis.impads.giragen.metrics.api.IMetrics;

import javax.inject.Inject;

public class MetricsStatsD implements IMetrics {

    private final MeterRegistry registry;

    @Inject
    public MetricsStatsD(MeterRegistry registry){
        this.registry = registry;
    }

    public Counter registerCounter(String name) {
        return Counter.builder(name)
                .register(registry);
    }
}

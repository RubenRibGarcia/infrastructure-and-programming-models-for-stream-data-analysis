package org.isel.thesis.impads.metrics.collector;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import org.isel.thesis.impads.metrics.collector.api.IMetrics;

import javax.inject.Inject;
import java.util.List;

public class Metrics implements IMetrics {

    private final MeterRegistry registry;

    @Inject
    public Metrics(MeterRegistry registry){
        this.registry = registry;
    }

    @Override
    public Counter registerCounter(String name, List<Tag> tags) {
        return Counter.builder(name)
                .tags(tags)
                .register(registry);
    }

    @Override
    public Counter registerCounter(String name, String... tagKeyValuePair) {
        return Counter.builder(name)
                .tags(tagKeyValuePair)
                .register(registry);
    }

    @Override
    public Timer registerTimer(String name, List<Tag> tags) {
        return Timer.builder(name)
                .tags(tags)
                .register(registry);
    }

    @Override
    public Timer registerTimer(String name, String... tagKeyValuePair) {
        return Timer.builder(name)
                .tags(tagKeyValuePair)
                .register(registry);
    }
}

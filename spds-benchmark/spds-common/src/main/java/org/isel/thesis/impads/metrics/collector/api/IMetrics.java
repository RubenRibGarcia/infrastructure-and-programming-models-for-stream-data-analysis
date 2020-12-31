package org.isel.thesis.impads.metrics.collector.api;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;

import java.util.List;

public interface IMetrics {

    MeterRegistry getRegistry();

    Counter registerCounter(String name, List<Tag> tags);

    Counter registerCounter(String name, String... tagKeyValuePair);

    Timer registerTimer(String name, List<Tag> tags);

    Timer registerTimer(String name, String... tagKeyValuePair);
}

package org.isel.thesis.impads.giragen.metrics.api;

import io.micrometer.core.instrument.Counter;

public interface IMetrics {

    Counter registerCounter(String name);
}

package org.isel.thesis.impads.giragen.metrics.api;

import java.util.function.Supplier;

public interface IServicesMetrics {

    <R> Supplier<R> withMetricCounter(IRegisterCounterMetric registerCounterMetric, WithCounterMetric<R> fn);
}

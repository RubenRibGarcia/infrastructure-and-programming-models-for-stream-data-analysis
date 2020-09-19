package org.isel.thesis.impads.giragen.metrics.func;

import org.isel.thesis.impads.giragen.metrics.api.IMetrics;
import org.isel.thesis.impads.giragen.metrics.api.IRegisterCounterMetric;
import org.isel.thesis.impads.giragen.metrics.api.IServicesMetrics;
import org.isel.thesis.impads.giragen.metrics.api.WithCounterMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.function.Supplier;

public class ServicesMetrics implements IServicesMetrics {

    private static final Logger logger = LoggerFactory.getLogger(ServicesMetrics.class);

    private final IMetrics metrics;

    @Inject
    public ServicesMetrics(IMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public <R> Supplier<R> withMetricCounter(IRegisterCounterMetric registerCounterMetric, WithCounterMetric<R> fn) {
        return () -> {
            R rvalue = fn.apply();
            metrics.registerCounter(registerCounterMetric);
            return rvalue;
        };
    }
}

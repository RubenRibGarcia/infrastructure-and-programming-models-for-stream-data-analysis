package org.isel.thesis.impads.giragen.metrics.func;

import io.micrometer.core.instrument.Counter;
import org.isel.thesis.impads.giragen.metrics.api.IMetrics;
import org.isel.thesis.impads.giragen.metrics.api.IServicesMetrics;
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

    public MetricsCounter withMetricCounter(String name) {
        return MetricsCounter.newMetricsCounter(metrics.registerCounter(name));
    }

    public static final class MetricsCounter {
        private final Counter counter;

        MetricsCounter(Counter counter) {
            this.counter = counter;
        }

        static MetricsCounter newMetricsCounter(Counter counter) {
            return new MetricsCounter(counter);
        }

        public Counter getCounter() {
            return counter;
        }

        public <R> R execute(Supplier<R> supplier) {
            R rvalue = supplier.get();
            counter.increment();
            return rvalue;
        }
    }
}

package org.isel.thesis.impads.flink.metrics;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;

public class ObservableFilterFunction<T extends Observable<?>> extends RichFilterFunction<T> {

    private final MetricsCollectorConfiguration config;
    private final RichFilterFunction<T> filterFunction;

    private FlinkObservableMetricsCollector collector;

    private ObservableFilterFunction(MetricsCollectorConfiguration config
            , RichFilterFunction<T> filterFunction) {
        this.config = config;
        this.filterFunction = filterFunction;
    }

    public static <T extends Observable<?>> ObservableFilterFunction<T> observe(MetricsCollectorConfiguration config
            , RichFilterFunction<T> filterFunction) {
        return new ObservableFilterFunction<>(config, filterFunction);
    }

    public static <T extends Observable<?>> ObservableFilterFunction<T> observe(MetricsCollectorConfiguration config
            , FilterFunction<T> filterFunction) {
        return new ObservableFilterFunction<>(config, new RichFilterFunction<T>() {
            @Override
            public boolean filter(T t) throws Exception {
                return filterFunction.filter(t);
            }
        });
    }

    @Override
    public boolean filter(T t) throws Exception {
        boolean retain = filterFunction.filter(t);

        if (!retain) {
            collector.collect(t);
        }

        return retain;
    }

    @Override
    public void open(Configuration parameters) throws Exception {

        collector = new FlinkObservableMetricsCollector(config);

        if (filterFunction != null) {
            filterFunction.open(parameters);
        }
    }
}

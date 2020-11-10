package org.isel.thesis.impads.flink.metrics;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;

public class ObservableSinkFunction<T extends Observable<?>> extends RichSinkFunction<T> {

    private final MetricsCollectorConfiguration config;
    private final RichSinkFunction<T> sinkFunction;

    private FlinkObservableMetricsCollector collector;

    private ObservableSinkFunction(final MetricsCollectorConfiguration config
            , final RichSinkFunction<T> sinkFunction) {
        this.config = config;
        this.sinkFunction = sinkFunction;
    }

    public static <T extends Observable<?>> ObservableSinkFunction<T> observe(MetricsCollectorConfiguration config) {
        return observe(config, null);
    }

    public static <T extends Observable<?>> ObservableSinkFunction<T> observe(MetricsCollectorConfiguration config
            , RichSinkFunction<T> sinkFunction) {
        return new ObservableSinkFunction<>(config
                , sinkFunction);
    }

    @Override
    public void invoke(T value, Context context) throws Exception {

        collector.collect(value);

        if (sinkFunction != null) {
            sinkFunction.invoke(value, context);
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {

        collector = new FlinkObservableMetricsCollector(config);

        if (sinkFunction != null) {
            sinkFunction.open(parameters);
        }
    }
}

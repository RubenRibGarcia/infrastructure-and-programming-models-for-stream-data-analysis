package org.isel.thesis.impads.flink.metrics;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;

public class ObservableSinkFunction<T extends Observable<?>> extends RichSinkFunction<T> {

    private static final long serialVersionUID = 1L;

    private final MetricsCollectorConfiguration config;
    private final RichSinkFunction<T> sinkFunction;
    private final String[] tags;

    private FlinkObservableMetricsCollector collector;

    private ObservableSinkFunction(final MetricsCollectorConfiguration config
            , final RichSinkFunction<T> sinkFunction
            , final String... tags) {
        this.config = config;
        this.sinkFunction = sinkFunction;
        this.tags = tags;
    }

    public static <T extends Observable<?>> ObservableSinkFunction<T> observe(MetricsCollectorConfiguration config
            , String... tags) {
        return observe(config, tags);
    }

    public static <T extends Observable<?>> ObservableSinkFunction<T> observe(MetricsCollectorConfiguration config
            , RichSinkFunction<T> sinkFunction
            , String... tags) {
        return new ObservableSinkFunction<>(config
                , sinkFunction
                , tags);
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

        collector = new FlinkObservableMetricsCollector(config, tags);

        if (sinkFunction != null) {
            sinkFunction.open(parameters);
        }
    }
}

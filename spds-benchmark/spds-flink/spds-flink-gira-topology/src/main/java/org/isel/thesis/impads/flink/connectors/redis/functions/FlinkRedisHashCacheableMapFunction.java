package org.isel.thesis.impads.flink.connectors.redis.functions;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;

public abstract class FlinkRedisHashCacheableMapFunction<IN, OUT, T, R>
        extends RichMapFunction<IN, OUT> {

    private static final long serialVersionUID = 1L;

    protected RedisHashCacheableMapFunction<T, R> function;

    protected FlinkRedisHashCacheableMapFunction(RedisHashCacheableMapFunction<T, R> function) {
        this.function = function;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.function.open();
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        if (function != null) {
            function.close();
        }
        super.close();
    }
}

package org.isel.thesis.impads.kafka.stream.connectors.redis;

import org.apache.kafka.streams.kstream.ValueMapper;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;
import org.isel.thesis.impads.io.OpenCloseable;

public abstract class KafkaStreamRedisHashCacheableMapValuesFunction<V, VR, T, R> implements ValueMapper<V, VR>, OpenCloseable {

    protected final RedisHashCacheableMapFunction<T,R> function;

    public KafkaStreamRedisHashCacheableMapValuesFunction(final RedisHashCacheableMapFunction<T,R> function) {
        this.function = function;
    }

    @Override
    public void open() throws Exception {
        if (function != null) {
            this.function.open();
        }
    }

    @Override
    public void close() throws Exception {
        if (function != null) {
            this.function.close();
        }
    }
}

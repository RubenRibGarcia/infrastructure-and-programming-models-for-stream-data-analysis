package org.isel.thesis.impads.connectors.redis;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.isel.thesis.impads.connectors.redis.api.CacheKeySelector;
import org.isel.thesis.impads.connectors.redis.api.RedisKeyHashField;
import org.isel.thesis.impads.connectors.redis.api.RedisHashMapper;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationBase;
import org.isel.thesis.impads.metrics.FactoryMetrics;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Duration;
import java.util.function.Function;

public class RedisHashCacheableMapFunction <T, R>
        extends BaseRedisFunction
        implements Function<T,R>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisHashCacheableMapFunction.class);

    private final CacheKeySelector<T, RedisKeyHashField> cacheKeySelector;
    private final RedisHashMapper<T, R> mapper;
    private final MetricsCollectorConfiguration metricsCollectorConfiguration;

    private Cache<RedisKeyHashField, R> cache;

    private RedisHashCacheableMapFunction(RedisConfigurationBase config
            , CacheKeySelector<T, RedisKeyHashField> cacheKeySelector
            , RedisHashMapper<T, R> mapper
            , MetricsCollectorConfiguration metricsCollectorConfiguration) {
        super(config);
        this.cacheKeySelector = cacheKeySelector;
        this.mapper = mapper;
        this.metricsCollectorConfiguration = metricsCollectorConfiguration;
    }

    public static <T, R> RedisHashCacheableMapFunction<T, R> newMapper(final RedisConfigurationBase redisConfigurationBase
            , final CacheKeySelector<T, RedisKeyHashField> cacheKeySelector
            , final RedisHashMapper<T, R> mapper) {
        return new RedisHashCacheableMapFunction<>(redisConfigurationBase, cacheKeySelector, mapper, null);
    }

    public static <T, R> RedisHashCacheableMapFunction<T, R> newMapperMonitored(final RedisConfigurationBase redisConfigurationBase
            , final CacheKeySelector<T, RedisKeyHashField> cacheKeySelector
            , final RedisHashMapper<T, R> mapper
            , final MetricsCollectorConfiguration metrics) {
        return new RedisHashCacheableMapFunction<>(redisConfigurationBase, cacheKeySelector, mapper, metrics);
    }

    @Override
    public void open() throws Exception {
        super.open();
        this.cache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofHours(1))
                .recordStats()
                .build();

        if (metricsCollectorConfiguration != null) {
            CaffeineCacheMetrics.monitor(FactoryMetrics.newMetrics(metricsCollectorConfiguration).getRegistry()
                    , this.cache
                    , "redis.mapper.cache");
        }
    }

    @Override
    public R apply(T t) {
        final RedisKeyHashField key = cacheKeySelector.getKey(t);

        return cache.get(key, k -> mapper.map(commands, key, t));
    }
}

package org.isel.thesis.impads.connectors.redis;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.isel.thesis.impads.connectors.redis.api.CacheKeySelector;
import org.isel.thesis.impads.connectors.redis.api.RedisHashLoader;
import org.isel.thesis.impads.connectors.redis.api.RedisKeyHashField;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


public class RedisHashCacheableLoaderFunction<I, O>
        extends BaseRedisFunction<I, O>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisHashCacheableLoaderFunction.class);

    private CacheKeySelector<RedisKeyHashField, I> cacheKeySelector;
    private RedisHashLoader<O> loader;
    private Cache<RedisKeyHashField, O> cache;

    private RedisHashCacheableLoaderFunction(final RedisConfigurationBase redisConfigurationBase
            , final CacheKeySelector<RedisKeyHashField, I> cacheKeySelector
            , final RedisHashLoader<O> loader) {
        super(redisConfigurationBase);
        this.cacheKeySelector = cacheKeySelector;
        this.loader = loader;
    }

    public static <I, O> RedisHashCacheableLoaderFunction<I, O> loader(final RedisConfigurationBase redisConfigurationBase
            , final CacheKeySelector<RedisKeyHashField, I> cacheKeySelector
            , final RedisHashLoader<O> loader) {
        return new RedisHashCacheableLoaderFunction<>(redisConfigurationBase, cacheKeySelector, loader);
    }

    public void open() throws Exception {
        super.open();
        this.cache = Caffeine.newBuilder()
                .maximumSize(100)
                .build();
    }

    public O load(I i) throws Exception {
        final RedisKeyHashField key = cacheKeySelector.getKey(i);

        return cache.get(key, (k) -> loader.load(commands));
    }
}

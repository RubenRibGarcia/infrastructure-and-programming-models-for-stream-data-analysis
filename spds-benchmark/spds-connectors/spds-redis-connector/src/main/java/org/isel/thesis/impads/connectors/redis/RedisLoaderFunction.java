package org.isel.thesis.impads.connectors.redis;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.isel.thesis.impads.connectors.redis.api.CacheKeySelector;
import org.isel.thesis.impads.connectors.redis.api.RedisHashLoader;
import org.isel.thesis.impads.connectors.redis.api.RedisKeyHashField;
import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class RedisLoaderFunction<I, O>
        extends BaseRedisFunction<I, O>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisHashCacheableLoaderFunction.class);

    private RedisHashLoader<O> loader;

    private RedisLoaderFunction(final RedisConfigurationBase redisConfigurationBase
            , final RedisHashLoader<O> loader) {
        super(redisConfigurationBase);
        this.loader = loader;
    }

    public static <I, O> RedisLoaderFunction<I, O> loader(final RedisConfigurationBase redisConfigurationBase
            , final RedisHashLoader<O> loader) {
        return new RedisLoaderFunction<>(redisConfigurationBase, loader);
    }

    public O load(I i) throws Exception {
        return loader.load(commands);
    }
}

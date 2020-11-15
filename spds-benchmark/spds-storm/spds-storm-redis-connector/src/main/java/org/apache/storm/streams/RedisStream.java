package org.apache.storm.streams;

import org.apache.storm.streams.operations.BiFunction;
import org.apache.storm.streams.processors.RedisMapProcessor;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;
import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainer;

public final class RedisStream {

    public static <T, R> Stream<T> map(Stream<R> stream
            , JedisPoolConfig jedisPoolConfig
            , BiFunction<RedisCommandsContainer, R, T> function) {
        return new Stream<>(stream.streamBuilder, stream.addProcessorNode(new RedisMapProcessor<>(jedisPoolConfig, function), Stream.VALUE));
    }
}

package org.apache.storm.streams.processors;

import org.apache.storm.streams.operations.BiFunction;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;
import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainer;
import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainerBuilder;

public class RedisMapProcessor<T, R> implements Processor<T> {

    private final JedisPoolConfig config;
    private final BiFunction<RedisCommandsContainer, T, R> function;

    private ProcessorContext context;
    private RedisCommandsContainer container;


    public RedisMapProcessor(JedisPoolConfig config
            , BiFunction<RedisCommandsContainer, T, R> function) {
        this.config = config;
        this.function = function;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        this.container = RedisCommandsContainerBuilder.build(config);
    }

    @Override
    public void execute(T input, String streamId) {
        context.forward(function.apply(container, input));
    }

    @Override
    public void punctuate(String stream) {
        //NOOP
    }
}

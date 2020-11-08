package org.apache.flink.streaming.connectors.redis;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.container.RedisCommandsContainer;
import org.apache.flink.streaming.connectors.redis.common.container.RedisCommandsContainerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RedisProcessFunction<IN, OUT> extends ProcessFunction<IN, OUT> {

    private static final Logger LOG = LoggerFactory.getLogger(RedisProcessFunction.class);

    private FlinkJedisConfigBase flinkJedisConfigBase;
    protected RedisCommandsContainer redisCommandsContainer;

    protected RedisProcessFunction(FlinkJedisConfigBase flinkJedisConfigBase) {
        this.flinkJedisConfigBase = flinkJedisConfigBase;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        try {
            this.redisCommandsContainer = RedisCommandsContainerBuilder.build(this.flinkJedisConfigBase);
            this.redisCommandsContainer.open();
        } catch (Exception e) {
            LOG.error("Redis has not been properly initialized: ", e);
            throw e;
        }
    }
}

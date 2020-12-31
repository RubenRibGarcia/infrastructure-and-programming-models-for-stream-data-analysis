package org.isel.thesis.impads.connectors.redis;

import org.isel.thesis.impads.connectors.redis.common.RedisConfigurationBase;
import org.isel.thesis.impads.connectors.redis.container.RedisCommandsContainer;
import org.isel.thesis.impads.connectors.redis.container.RedisCommandsContainerBuilder;
import org.isel.thesis.impads.io.OpenCloseable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

public abstract class BaseRedisFunction implements Serializable, OpenCloseable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(BaseRedisFunction.class);

    protected RedisConfigurationBase config;
    protected transient RedisCommandsContainer commands;

    protected BaseRedisFunction(RedisConfigurationBase config) {
        Objects.requireNonNull(config, "Redis config should not be null");
        this.config = config;
    }

    @Override
    public void open() throws Exception {
        try {
            this.commands = RedisCommandsContainerBuilder.build(this.config);
            this.commands.open();
        } catch (Exception e) {
            LOG.error("Redis has not been properly initialized: ", e);
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        if (commands != null) {
            commands.close();
        }
    }
}

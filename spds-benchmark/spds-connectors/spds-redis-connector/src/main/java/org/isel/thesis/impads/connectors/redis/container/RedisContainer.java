package org.isel.thesis.impads.connectors.redis.container;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.isel.thesis.impads.io.OpenCloseable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;


public class RedisContainer implements RedisCommandsContainer, OpenCloseable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisContainer.class);

    private transient final RedisClient redisClient;
    private transient StatefulRedisConnection<String, String> connection;
    private transient RedisCommands<String, String> commands;

    public RedisContainer(RedisClient redisClient) {
        Objects.requireNonNull(redisClient, "Redis Client can not be null");
        this.redisClient = redisClient;
    }

    @Override
    public void close() throws IOException {
        redisClient.shutdown();
    }

    @Override
    public void open() throws Exception {
        this.connection = redisClient.connect();
        this.commands = connection.sync();
    }

    @Override
    public String hget(String key, String hashField) {
        try {
            return commands.hget(key, hashField);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Cannot send Redis message with command HSET to key {} and hashField {} error message {}",
                        key, hashField, e.getMessage());
            }
            throw new RedisContainerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void rpush(final String listName, final String value) {
        try {
            commands.rpush(listName, value);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Cannot send Redis message with command RPUSH to list {} error message {}",
                        listName, e.getMessage());
            }
            throw new RedisContainerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void lpush(String listName, String value) {
        try {
            commands.lpush(listName, value);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Cannot send Redis message with command LPUSH to list {} error message {}",
                        listName, e.getMessage());
            }
            throw new RedisContainerException(e.getMessage(), e.getCause());
        }
    }
}

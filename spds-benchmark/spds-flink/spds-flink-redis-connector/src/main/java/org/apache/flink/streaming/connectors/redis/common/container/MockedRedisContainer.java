package org.apache.flink.streaming.connectors.redis.common.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MockedRedisContainer implements RedisCommandsContainer {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(MockedRedisContainer.class);


    @Override
    public void open() throws Exception {
        logger.info("Opening Mocked Redis container");
    }

    @Override
    public void hset(String key, String hashField, String value, Integer ttl) {

    }

    @Override
    public String hget(String key, String hashField) {
        logger.info("HGET Mocked");
        return "{}";
    }

    @Override
    public void hincrBy(String key, String hashField, Long value, Integer ttl) {

    }

    @Override
    public void rpush(String listName, String value) {

    }

    @Override
    public void lpush(String listName, String value) {

    }

    @Override
    public void sadd(String setName, String value) {

    }

    @Override
    public void publish(String channelName, String message) {

    }

    @Override
    public void set(String key, String value) {

    }

    @Override
    public void setex(String key, String value, Integer ttl) {

    }

    @Override
    public void pfadd(String key, String element) {

    }

    @Override
    public void zadd(String key, String score, String element) {

    }

    @Override
    public void zincrBy(String key, String score, String element) {

    }

    @Override
    public void zrem(String key, String element) {

    }

    @Override
    public void incrByEx(String key, Long value, Integer ttl) {

    }

    @Override
    public void decrByEx(String key, Long value, Integer ttl) {

    }

    @Override
    public void incrBy(String key, Long value) {

    }

    @Override
    public void decrBy(String key, Long value) {

    }

    @Override
    public void close() throws IOException {

    }
}

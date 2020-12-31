package org.isel.thesis.impads.connectors.redis.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MockedRedisContainer implements RedisCommandsContainer {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(MockedRedisContainer.class);


    @Override
    public void open() throws Exception {
        LOG.info("Opening Mocked Redis container");
    }

    @Override
    public String hget(String key, String hashField) {
        LOG.info("HGET mocked");
        return "{}";
    }

    @Override
    public void rpush(String listName, String value) {
        LOG.info("RPUSH mocked");
    }

    @Override
    public void lpush(String listName, String value) {
        LOG.info("LPUSH mocked");
    }

    @Override
    public void close() throws IOException {
        LOG.info("Closing Mocked Redis container");
    }
}

package org.isel.thesis.impads.flink.redis.connector;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisSink<IN> extends RichSinkFunction<IN> {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisSink.class);

    private final String hostname;
    private final int port;

    private transient JedisPool jedisPool;

    public RedisSink(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        jedisPool = new JedisPool(hostname, port);
    }

    @Override
    public void invoke(IN value, Context context) throws Exception {

    }

    private Jedis getInstance() {
        return jedisPool.getResource();
    }

    private void releaseInstance(final Jedis jedis) {
        if (jedis == null) {
            return;
        }
        try {
            jedis.close();
        } catch (Exception e) {
            LOG.error("Failed to close (return) instance to pool", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}

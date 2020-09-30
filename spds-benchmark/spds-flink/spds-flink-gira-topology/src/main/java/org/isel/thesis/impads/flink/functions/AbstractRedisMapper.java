package org.isel.thesis.impads.flink.functions;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import redis.clients.jedis.Jedis;

public abstract class AbstractRedisMapper<T, R>
        extends RichMapFunction<T, R> {

    private final String host;
    private final int port;

    protected Jedis jedis;

    public AbstractRedisMapper(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void close() throws Exception {
        if (jedis != null && jedis.isConnected()) {
            jedis.close();
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        jedis = new Jedis(host, port);
    }
}

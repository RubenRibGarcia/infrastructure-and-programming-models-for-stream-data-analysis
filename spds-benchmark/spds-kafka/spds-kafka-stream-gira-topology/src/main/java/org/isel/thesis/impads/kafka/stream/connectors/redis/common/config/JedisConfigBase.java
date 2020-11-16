package org.isel.thesis.impads.kafka.stream.connectors.redis.common.config;

import org.isel.thesis.impads.kafka.stream.connectors.redis.common.Util;

import java.io.Serializable;

public abstract class JedisConfigBase implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final int maxTotal;
    protected final int maxIdle;
    protected final int minIdle;
    protected final int connectionTimeout;
    protected final String password;

    protected JedisConfigBase(int connectionTimeout, int maxTotal, int maxIdle, int minIdle, String password) {
        Util.checkArgument(connectionTimeout >= 0, "connection timeout can not be negative");
        Util.checkArgument(maxTotal >= 0, "maxTotal value can not be negative");
        Util.checkArgument(maxIdle >= 0, "maxIdle value can not be negative");
        Util.checkArgument(minIdle >= 0, "minIdle value can not be negative");

        this.connectionTimeout = connectionTimeout;
        this.maxTotal = maxTotal;
        this.maxIdle = maxIdle;
        this.minIdle = minIdle;
        this.password = password;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public String getPassword() {
        return password;
    }
}

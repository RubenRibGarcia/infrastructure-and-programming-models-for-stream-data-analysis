package org.isel.thesis.impads.storm.redis.common.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Protocol;

import java.io.Serializable;

/**
 * Configuration for JedisPool.
 */
public class JedisPoolConfig implements Serializable {

    private String host;
    private int port;
    private int timeout;
    private int minIdle;
    private int maxIdle;
    private int maxTotal;

    // for serialization
    public JedisPoolConfig() {
    }

    public JedisPoolConfig(String host, int port, int timeout, int minIdle, int maxIdle, int maxTotal) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.minIdle = minIdle;
        this.maxIdle = maxIdle;
        this.maxTotal = maxTotal;
    }

    /**
     * Returns host.
     * @return hostname or IP
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns port.
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns timeout.
     * @return socket / connection timeout
     */
    public int getTimeout() {
        return timeout;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    /**
     * Builder for initializing JedisPoolConfig.
     */
    public static class Builder {
        private String host = Protocol.DEFAULT_HOST;
        private int port = Protocol.DEFAULT_PORT;
        private int timeout = Protocol.DEFAULT_TIMEOUT;
        private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;
        private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
        private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;

        /**
         * Sets host.
         * @param host host
         * @return Builder itself
         */
        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        /**
         * Sets port.
         * @param port port
         * @return Builder itself
         */
        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets timeout.
         * @param timeout timeout
         * @return Builder itself
         */
        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setMinIdle(int minIdle) {
            this.minIdle = minIdle;
            return this;
        }

        public Builder setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
            return this;
        }

        public Builder setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        /**
         * Builds JedisPoolConfig.
         * @return JedisPoolConfig
         */
        public JedisPoolConfig build() {
            return new JedisPoolConfig(host, port, timeout, minIdle, maxIdle, maxTotal);
        }
    }
}

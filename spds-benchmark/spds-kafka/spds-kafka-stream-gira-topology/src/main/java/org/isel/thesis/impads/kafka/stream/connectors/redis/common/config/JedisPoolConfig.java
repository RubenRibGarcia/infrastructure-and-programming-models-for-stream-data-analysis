package org.isel.thesis.impads.kafka.stream.connectors.redis.common.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.Objects;

public class JedisPoolConfig extends JedisConfigBase {

    private static final long serialVersionUID = 1L;

    private final String host;
    private final int port;

    private JedisPoolConfig(String host, int port, int connectionTimeout
            , int maxTotal, int maxIdle, int minIdle) {
        super(connectionTimeout, maxTotal, maxIdle, minIdle);
        Objects.requireNonNull(host, "Host information should be presented");
        this.host = host;
        this.port = port;
    }

    /**
     * Returns host.
     *
     * @return hostname or IP
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns port.
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * Builder for initializing  {@link JedisPoolConfig}.
     */
    public static class Builder {
        private String host;
        private int port = Protocol.DEFAULT_PORT;
        private int timeout = Protocol.DEFAULT_TIMEOUT;
        private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;
        private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
        private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;

        /**
         * Sets value for the {@code maxTotal} configuration attribute
         * for pools to be created with this configuration instance.
         *
         * @param maxTotal maxTotal the maximum number of objects that can be allocated by the pool, default value is 8
         * @return Builder itself
         */
        public Builder setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        /**
         * Sets value for the {@code maxIdle} configuration attribute
         * for pools to be created with this configuration instance.
         *
         * @param maxIdle the cap on the number of "idle" instances in the pool, default value is 8
         * @return Builder itself
         */
        public Builder setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
            return this;
        }

        /**
         * Sets value for the {@code minIdle} configuration attribute
         * for pools to be created with this configuration instance.
         *
         * @param minIdle the minimum number of idle objects to maintain in the pool, default value is 0
         * @return Builder itself
         */
        public Builder setMinIdle(int minIdle) {
            this.minIdle = minIdle;
            return this;
        }

        /**
         * Sets host.
         *
         * @param host host
         * @return Builder itself
         */
        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        /**
         * Sets port.
         *
         * @param port port, default value is 6379
         * @return Builder itself
         */
        public Builder setPort(int port) {
            this.port = port;
            return this;
        }


        /**
         * Builds JedisPoolConfig.
         *
         * @return JedisPoolConfig
         */
        public JedisPoolConfig build() {
            return new JedisPoolConfig(host, port, timeout, maxTotal, maxIdle, minIdle);
        }
    }
}

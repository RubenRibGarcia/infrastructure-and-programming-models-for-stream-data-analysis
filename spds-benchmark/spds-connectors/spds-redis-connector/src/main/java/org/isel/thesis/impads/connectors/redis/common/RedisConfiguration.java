package org.isel.thesis.impads.connectors.redis.common;

public class RedisConfiguration extends RedisConfigurationBase {

    private static final long serialVersionUID = 1L;

    private final String host;
    private final int port;

    private RedisConfiguration(String host, int port, boolean mocked) {
        super(mocked);
        this.host = host;
        this.port = port;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static final class Builder {

        private String host;
        private int port;
        private boolean mocked;

        private Builder() {

        }

        public Builder withHost(String host) {
            this.host = host;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder isMocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        public RedisConfiguration build() {
            return new RedisConfiguration(host, port, mocked);
        }
    }
}

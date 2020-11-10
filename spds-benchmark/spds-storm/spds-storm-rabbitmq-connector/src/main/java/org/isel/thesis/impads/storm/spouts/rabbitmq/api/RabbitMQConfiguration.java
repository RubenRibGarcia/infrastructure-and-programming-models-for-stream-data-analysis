package org.isel.thesis.impads.storm.spouts.rabbitmq.api;

public class RabbitMQConfiguration {

    private final String host;
    private final int port;
    private final String virtualHost;
    private final String username;
    private final String password;

    public RabbitMQConfiguration(String host, int port, String virtualHost, String username, String password) {
        this.host = host;
        this.port = port;
        this.virtualHost = virtualHost;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static final class Builder {
        private String host;
        private int port;
        private String virtualHost;
        private String username;
        private String password;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withHost(String host) {
            this.host = host;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withVirtualHost(String virtualHost) {
            this.virtualHost = virtualHost;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public RabbitMQConfiguration build() {
            return new RabbitMQConfiguration(host, port, virtualHost, username, password);
        }
    }
}

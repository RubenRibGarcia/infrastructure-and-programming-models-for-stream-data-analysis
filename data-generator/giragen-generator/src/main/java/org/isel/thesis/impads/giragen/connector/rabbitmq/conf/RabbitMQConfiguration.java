package org.isel.thesis.impads.giragen.connector.rabbitmq.conf;

import com.typesafe.config.Config;

public class RabbitMQConfiguration {

    private final Config config;

    private RabbitMQConfiguration(Config config) {
        this.config = config;
    }

    public static RabbitMQConfiguration load(Config config) {
        return new RabbitMQConfiguration(config);
    }

    public boolean isRabbitMQEnabled() {
        return this.config.getBoolean(RabbitMQConfigurationFields.RABBITMQ_ENABLED);
    }

    public String getRabbitMQHost() {
        return this.config.getString(RabbitMQConfigurationFields.RABBITMQ_HOST);
    }

    public int getRabbitMQPort() {
        return this.config.getInt(RabbitMQConfigurationFields.RABBITMQ_PORT);
    }

    public int getRabbitMQConnectionTimeoutMs() {
        return this.config.getInt(RabbitMQConfigurationFields.RABBITMQ_CONNECTION_TIMEOUT_MS);
    }

    public int getRabbitMQHandshakeTimeoutMs() {
        return this.config.getInt(RabbitMQConfigurationFields.RABBITMQ_HANDSHAKE_TIMEOUT_MS);
    }
    
    public String getRabbitMQUsername() {
        return this.config.getString(RabbitMQConfigurationFields.RABBITMQ_USERNAME);
    }

    public String getRabbitMQPassword() {
        return this.config.getString(RabbitMQConfigurationFields.RABBITMQ_PASSWORD);
    }

    public static final class RabbitMQConfigurationFields {
        private static final String RABBITMQ_PREFIX = "rabbitmq.";
        public static final String RABBITMQ_ENABLED = RABBITMQ_PREFIX + "enabled";
        public static final String RABBITMQ_HOST = RABBITMQ_PREFIX + "host";
        public static final String RABBITMQ_PORT = RABBITMQ_PREFIX + "port";
        public static final String RABBITMQ_CONNECTION_TIMEOUT_MS = RABBITMQ_PREFIX + "connection_timeout_ms";
        public static final String RABBITMQ_HANDSHAKE_TIMEOUT_MS = RABBITMQ_PREFIX + "handshake_timeout_ms";
        public static final String RABBITMQ_USERNAME = RABBITMQ_PREFIX + "username";
        public static final String RABBITMQ_PASSWORD = RABBITMQ_PREFIX + "password";
    }


}

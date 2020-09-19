package org.isel.thesis.impads.kafka.connect.rabbitmq.conf;

import org.apache.kafka.common.config.AbstractConfig;

public class RabbitMQSourceConnectorConfiguration {

    private final AbstractConfig config;

    private RabbitMQSourceConnectorConfiguration(AbstractConfig config) {
        this.config = config;
    }

    public static RabbitMQSourceConnectorConfiguration load(AbstractConfig config) {
        return new RabbitMQSourceConnectorConfiguration(config);
    }

    public String getKafkaTopic() {
        return config.getString(RabbitMQSourceConnectorConfigurationFields.KAFKA_TOPIC);
    }

    public String getRabbitMQUsername() {
        return config.getString(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_USERNAME);
    }

    public String getRabbitMQPassword() {
        return config.getString(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_PASSWORD);
    }

    public String getRabbitMQHost() {
        return config.getString(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_HOST);
    }

    public int getRabbitMQPort() {
        return config.getInt(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_PORT);
    }

    public String getRabbitMQQueue() {
        return config.getString(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_QUEUE);
    }

    public static final class RabbitMQSourceConnectorConfigurationFields {
        private static final String KAFKA_PREFIX = "kafka.";
        public static final String KAFKA_TOPIC = KAFKA_PREFIX + "topic";
        private static final String RABBITMQ_PREFIX = "rabbitmq.";
        public static final String RABBITMQ_HOST = RABBITMQ_PREFIX + "host";
        public static final String RABBITMQ_PORT = RABBITMQ_PREFIX + "port";
        public static final String RABBITMQ_USERNAME = RABBITMQ_PREFIX + "username";
        public static final String RABBITMQ_PASSWORD = RABBITMQ_PREFIX + "password";
        public static final String RABBITMQ_QUEUE = RABBITMQ_PREFIX + "queue";
    }
}

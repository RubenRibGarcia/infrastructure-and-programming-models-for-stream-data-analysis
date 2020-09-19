package org.isel.thesis.impads.kafka.connect.rabbitmq.conf;

import java.util.Map;

import static org.isel.thesis.impads.kafka.connect.rabbitmq.conf.RabbitMQConfiguration.RabbitMqConfigurationFields.*;

public class RabbitMQConfiguration {

    private final Map<String, String> config;

    private RabbitMQConfiguration(Map<String, String> config) {
        this.config = config;
    }

    public static RabbitMQConfiguration load(Map<String, String> config) {
        return new RabbitMQConfiguration(config);
    }

    public String getKafkaTopic() {
        return config.get(KAFKA_TOPIC);
    }

    public String getRabbitMQHost() {
        return config.get(RABBITMQ_HOST);
    }

    public int getRabbitMQPort() {
        return Integer.parseInt(config.get(RABBITMQ_PORT));
    }

    public String getRabbitMQUsername() {
        return config.get(RABBITMQ_USERNAME);
    }

    public String getRabbitMQPassword() {
        return config.get(RABBITMQ_PASSWORD);
    }

    public String getRabbitMQQueue() {
        return config.get(RABBITMQ_QUEUE);
    }

    static final class RabbitMqConfigurationFields {
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


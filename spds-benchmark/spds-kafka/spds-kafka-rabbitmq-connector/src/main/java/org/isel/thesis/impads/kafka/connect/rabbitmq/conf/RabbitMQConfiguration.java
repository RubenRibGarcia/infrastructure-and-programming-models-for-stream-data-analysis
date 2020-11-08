package org.isel.thesis.impads.kafka.connect.rabbitmq.conf;

import java.util.Map;

public class RabbitMQConfiguration {

    private final Map<String, String> config;

    private RabbitMQConfiguration(Map<String, String> config) {
        this.config = config;
    }

    public static RabbitMQConfiguration load(Map<String, String> config) {
        return new RabbitMQConfiguration(config);
    }

    public String getKafkaTopic() {
        return config.get(RabbitMQConfigurationFields.KAFKA_TOPIC);
    }

    public String getRabbitMQHost() {
        return config.get(RabbitMQConfigurationFields.RABBITMQ_HOST);
    }

    public int getRabbitMQPort() {
        return Integer.parseInt(config.get(RabbitMQConfigurationFields.RABBITMQ_PORT));
    }

    public String getRabbitMQUsername() {
        return config.get(RabbitMQConfigurationFields.RABBITMQ_USERNAME);
    }

    public String getRabbitMQPassword() {
        return config.get(RabbitMQConfigurationFields.RABBITMQ_PASSWORD);
    }

    public String getRabbitMQQueue() {
        return config.get(RabbitMQConfigurationFields.RABBITMQ_QUEUE);
    }
}


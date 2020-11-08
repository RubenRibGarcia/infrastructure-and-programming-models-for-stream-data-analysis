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
        return config.getString(RabbitMQConfigurationFields.KAFKA_TOPIC);
    }

    public String getRabbitMQUsername() {
        return config.getString(RabbitMQConfigurationFields.RABBITMQ_USERNAME);
    }

    public String getRabbitMQPassword() {
        return config.getString(RabbitMQConfigurationFields.RABBITMQ_PASSWORD);
    }

    public String getRabbitMQHost() {
        return config.getString(RabbitMQConfigurationFields.RABBITMQ_HOST);
    }

    public int getRabbitMQPort() {
        return config.getInt(RabbitMQConfigurationFields.RABBITMQ_PORT);
    }

    public String getRabbitMQQueue() {
        return config.getString(RabbitMQConfigurationFields.RABBITMQ_QUEUE);
    }
}

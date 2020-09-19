package org.isel.thesis.impads.storm.spouts.rabbitmq.conf;

import com.typesafe.config.Config;

import java.io.Serializable;

import static org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration.RabbitMqConfigurationFields.RABBITMQ_HOST;
import static org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration.RabbitMqConfigurationFields.RABBITMQ_PASSWORD;
import static org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration.RabbitMqConfigurationFields.RABBITMQ_PORT;
import static org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration.RabbitMqConfigurationFields.RABBITMQ_USERNAME;

public class RabbitMQConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Config config;

    private RabbitMQConfiguration(Config config) {
        this.config = config;
    }

    public static RabbitMQConfiguration initializeRabbitMQConfiguration(Config config) {
        return new RabbitMQConfiguration(config);
    }


    public String getRabbitMQHost() {
        return config.getString(RABBITMQ_HOST);
    }

    public int getRabbitMQPort() {
        return config.getInt(RABBITMQ_PORT);
    }

    public String getRabbitMQUsername() {
        return config.getString(RABBITMQ_USERNAME);
    }

    public String getRabbitMQPassword() {
        return config.getString(RABBITMQ_PASSWORD);
    }

    static final class RabbitMqConfigurationFields {
        private static final String RABBITMQ_PREFIX = "rabbitmq.";
        public static final String RABBITMQ_HOST = RABBITMQ_PREFIX + "host";
        public static final String RABBITMQ_PORT = RABBITMQ_PREFIX + "port";
        public static final String RABBITMQ_USERNAME = RABBITMQ_PREFIX + "username";
        public static final String RABBITMQ_PASSWORD = RABBITMQ_PREFIX + "password";
    }
}

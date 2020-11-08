package org.isel.thesis.impads.flink.rabbitmq.connector.api;

public class RabbitMQConfigurationFields {

        private static final String RABBITMQ_PREFIX = "rabbitmq.";
        public static final String RABBITMQ_HOST = RABBITMQ_PREFIX + "host";
        public static final String RABBITMQ_PORT = RABBITMQ_PREFIX + "port";
        public static final String RABBITMQ_VIRTUAL_HOST = RABBITMQ_PREFIX + "virtual_host";
        public static final String RABBITMQ_USERNAME = RABBITMQ_PREFIX + "username";
        public static final String RABBITMQ_PASSWORD = RABBITMQ_PREFIX + "password";
}

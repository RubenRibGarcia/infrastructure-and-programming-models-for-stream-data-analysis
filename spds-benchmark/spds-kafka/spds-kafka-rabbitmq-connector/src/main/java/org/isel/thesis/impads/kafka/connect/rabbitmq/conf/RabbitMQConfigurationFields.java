package org.isel.thesis.impads.kafka.connect.rabbitmq.conf;

public final class RabbitMQConfigurationFields {

    private static final String KAFKA_PREFIX = "kafka.";
    public static final String KAFKA_TOPIC = KAFKA_PREFIX + "topic";
    private static final String RABBITMQ_PREFIX = "rabbitmq.";
    public static final String RABBITMQ_HOST = RABBITMQ_PREFIX + "host";
    public static final String RABBITMQ_PORT = RABBITMQ_PREFIX + "port";
    public static final String RABBITMQ_USERNAME = RABBITMQ_PREFIX + "username";
    public static final String RABBITMQ_PASSWORD = RABBITMQ_PREFIX + "password";
    public static final String RABBITMQ_QUEUE = RABBITMQ_PREFIX + "queue";
}

package org.isel.thesis.impads.flink.rabbitmq.connector;

import com.typesafe.config.Config;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.IRMQQueue;

public class DataStreamRMQSink<T> extends RMQSink<T> {

    private DataStreamRMQSink(RMQConnectionConfig rmqConnectionConfig
            , String queueName
            , SerializationSchema<T> schema) {
        super(rmqConnectionConfig, queueName, schema);
    }

    public static <T> DataStreamRMQSink<T> newRabbitMQSink(Config config
            , final IRMQQueue queue
            , final SerializationSchema<T> schema) {

        final RMQConnectionConfig connectionConfig = configureRMQConnection(config);

        return new DataStreamRMQSink<>(connectionConfig, queue.getQueueName(), schema);
    }

    private static RMQConnectionConfig configureRMQConnection(Config config) {
        final RMQConnectionConfig.Builder builderConfig = new RMQConnectionConfig.Builder()
                .setHost(getHost(config))
                .setPort(getPort(config))
                .setVirtualHost(getVirtualHost(config))
                .setUserName(getUsername(config))
                .setPassword(getPassword(config));

        return builderConfig.build();
    }

    private static String getHost(Config config) {
        return config.getString(RabbitMQConnectionConfigurationFields.RABBITMQ_HOST);
    }

    private static int getPort(Config config) {
        return config.getInt(RabbitMQConnectionConfigurationFields.RABBITMQ_PORT);
    }

    private static String getVirtualHost(Config config) {
        return config.getString(RabbitMQConnectionConfigurationFields.RABBITMQ_VIRTUAL_HOST);
    }

    private static String getUsername(Config config) {
        return config.getString(RabbitMQConnectionConfigurationFields.RABBITMQ_USERNAME);
    }

    private static String getPassword(Config config) {
        return config.getString(RabbitMQConnectionConfigurationFields.RABBITMQ_PASSWORD);
    }

    private static final class RabbitMQConnectionConfigurationFields {
        private static final String RABBITMQ_PREFIX = "rabbitmq.";
        public static final String RABBITMQ_HOST = RABBITMQ_PREFIX + "host";
        public static final String RABBITMQ_PORT = RABBITMQ_PREFIX + "port";
        public static final String RABBITMQ_VIRTUAL_HOST = RABBITMQ_PREFIX + "virtual_host";
        public static final String RABBITMQ_USERNAME = RABBITMQ_PREFIX + "username";
        public static final String RABBITMQ_PASSWORD = RABBITMQ_PREFIX + "password";
    }
}

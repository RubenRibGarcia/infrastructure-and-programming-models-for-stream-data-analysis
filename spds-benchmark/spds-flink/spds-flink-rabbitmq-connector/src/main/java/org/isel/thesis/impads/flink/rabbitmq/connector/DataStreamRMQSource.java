package org.isel.thesis.impads.flink.rabbitmq.connector;

import com.typesafe.config.Config;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.IRMQQueue;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.RabbitMQConnectionConfigurationFields;

public class DataStreamRMQSource<T> extends RMQSource<T> {

    private DataStreamRMQSource(RMQConnectionConfig rmqConnectionConfig
            , String queueName
            , DeserializationSchema<T> deserializationSchema) {
        super(rmqConnectionConfig, queueName, deserializationSchema);
    }

    public static <T> DataStreamRMQSource<T> newRabbitMQSource(final Config config
            , final IRMQQueue queue
            , final DeserializationSchema<T> deserializationSchema) {
        final RMQConnectionConfig connectionConfig = configureRMQConnection(config);

        return new DataStreamRMQSource<T>(connectionConfig, queue.getQueueName(), deserializationSchema);
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

}

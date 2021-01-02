package org.isel.thesis.impads.flink.rabbitmq.connector;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.IRMQQueue;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.RabbitMQConfiguration;

public class DataStreamRMQSource<T> extends RMQSource<T> {

    private DataStreamRMQSource(RMQConnectionConfig rmqConnectionConfig
            , String queueName
            , DeserializationSchema<T> deserializationSchema) {
        super(rmqConnectionConfig, queueName, deserializationSchema);
    }

    public static <T> DataStreamRMQSource<T> newRabbitMQSource(final RabbitMQConfiguration config
            , final IRMQQueue queue
            , final DeserializationSchema<T> deserializationSchema) {
        final RMQConnectionConfig connectionConfig = configureRMQConnection(config);

        return new DataStreamRMQSource<T>(connectionConfig, queue.getQueueName(), deserializationSchema);
    }

    private static RMQConnectionConfig configureRMQConnection(RabbitMQConfiguration config) {
        final RMQConnectionConfig.Builder builderConfig = new RMQConnectionConfig.Builder()
                .setHost(config.getHost())
                .setPort(config.getPort())
                .setVirtualHost(config.getVirtualHost())
                .setUserName(config.getUsername())
                .setPassword(config.getPassword());

        return builderConfig.build();
    }

}

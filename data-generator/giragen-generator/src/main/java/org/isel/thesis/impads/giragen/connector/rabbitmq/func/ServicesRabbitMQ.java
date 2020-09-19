package org.isel.thesis.impads.giragen.connector.rabbitmq.func;

import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQClient;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQConnection;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IServicesRabbitMQ;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.WithRabbitMQClientCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

public class ServicesRabbitMQ implements IServicesRabbitMQ {

    private static final Logger logger = LoggerFactory.getLogger(ServicesRabbitMQ.class);

    private final IRabbitMQConnection rabbitMQConnection;

    @Inject
    public ServicesRabbitMQ(IRabbitMQConnection rabbitMQConnection) {
        this.rabbitMQConnection = rabbitMQConnection;
    }

    @Override
    public <R> R withRabbitMQClient(String key, WithRabbitMQClientCallback<R> fn) {
        return withRabbitMQClient(key, this.rabbitMQConnection, fn).get();
    }

    @Override
    public IRabbitMQClient createRabbitMQClient(String key) {
        try {
            return rabbitMQConnection.getClient(key);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public IRabbitMQClient createRabbitMQClient() {
        return this.createRabbitMQClient(UUID.randomUUID().toString());
    }

    public static <R> Supplier<R> withRabbitMQClient(String key, IRabbitMQConnection rabbitMQConnection, WithRabbitMQClientCallback<R> fn) {
        return () -> {
            R rvalue = null;

            try {
                final IRabbitMQClient client = rabbitMQConnection.getClient(key);

                rvalue = fn.apply(client);

                rabbitMQConnection.closeClient(key);
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return rvalue;
        };
    }
}

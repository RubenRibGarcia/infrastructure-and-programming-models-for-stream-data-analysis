package org.isel.thesis.impads.giragen.connector.rabbitmq.func;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQConnection;
import org.isel.thesis.impads.giragen.connector.rabbitmq.conf.RabbitMQConfiguration;
import org.isel.thesis.impads.giragen.connector.rabbitmq.mock.MockConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;


public final class RabbitMQConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConnectionFactory.class);

    public static Optional<IRabbitMQConnection> createNewConnection(RabbitMQConfiguration configuration) {
        Optional<IRabbitMQConnection> rabbitMQConnection = Optional.empty();
        logger.info("Creating connection to RabbitMQ at host: {}, port: {}", configuration.getRabbitMQHost()
                , configuration.getRabbitMQPort());

        ConnectionFactory connFactory = createConnectionFactory(configuration);

        try {
            final Connection connection = connFactory.newConnection();
            rabbitMQConnection = Optional.of(new RabbitMQConnection(connection));
        } catch (IOException | TimeoutException e) {
            logger.error(e.getMessage(), e);
        }

        return rabbitMQConnection;
    }

    private static ConnectionFactory createConnectionFactory(RabbitMQConfiguration configuration) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(configuration.getRabbitMQHost());
        connectionFactory.setConnectionTimeout(configuration.getRabbitMQConnectionTimeoutMs());
        connectionFactory.setHandshakeTimeout(configuration.getRabbitMQHandshakeTimeoutMs());
        connectionFactory.setUsername(configuration.getRabbitMQUsername());
        connectionFactory.setPassword(configuration.getRabbitMQPassword());
        connectionFactory.setPort(configuration.getRabbitMQPort());

        return connectionFactory;
    }
}

package org.isel.thesis.impads.storm.spouts.rabbitmq.func;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.RabbitMQConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

public final class RabbitMQConnectionFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQConnectionFactory.class);

    public static Connection createNewConnection(RabbitMQConfiguration configuration) throws IOException, TimeoutException {
        ConnectionFactory connFactory = createConnectionFactory(configuration);

        return connFactory.newConnection();
    }

    private static ConnectionFactory createConnectionFactory(RabbitMQConfiguration configuration) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(configuration.getHost());
        connectionFactory.setUsername(configuration.getUsername());
        connectionFactory.setPassword(configuration.getPassword());
        connectionFactory.setPort(configuration.getPort());

        return connectionFactory;
    }
}

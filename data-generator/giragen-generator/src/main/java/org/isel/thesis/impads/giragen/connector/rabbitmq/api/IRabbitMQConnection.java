package org.isel.thesis.impads.giragen.connector.rabbitmq.api;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IRabbitMQConnection {

    IRabbitMQClient getClient(String key) throws IOException;

    Connection getConnection();

    void closeClient(String key) throws IOException, TimeoutException;
}

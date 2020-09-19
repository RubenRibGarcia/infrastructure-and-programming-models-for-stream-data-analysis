package org.isel.thesis.impads.giragen.connector.rabbitmq.api;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IRabbitMQClient {

    void publishMessage(IRabbitMQQueue queue, IRabbitMQMessage message) throws IOException;

    void declareQueue(IRabbitMQQueue queue) throws IOException;

    Channel getChannel();

    void closeChannel() throws IOException, TimeoutException;
}

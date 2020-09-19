package org.isel.thesis.impads.giragen.connector.rabbitmq.func;

import com.rabbitmq.client.Channel;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQClient;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQMessage;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQQueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQClient implements IRabbitMQClient {

    private final Channel channel;

    public RabbitMQClient(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void declareQueue(IRabbitMQQueue queue) throws IOException {
        this.channel.queueDeclare(queue.getQueueName(), true, false, false, null);
    }

    @Override
    public void publishMessage(IRabbitMQQueue queue, IRabbitMQMessage message) throws IOException {
        this.channel.basicPublish("", queue.getQueueName(), null, message.getMessage().getBytes());
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void closeChannel() throws IOException, TimeoutException {
        this.channel.close();
    }
}

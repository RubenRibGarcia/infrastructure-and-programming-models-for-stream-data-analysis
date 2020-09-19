package org.isel.thesis.impads.giragen.connector.rabbitmq.func;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQClient;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMQConnection implements IRabbitMQConnection {

    private final Connection connection;
    private final Map<String, IRabbitMQClient> clientMap;

    public RabbitMQConnection(Connection connection) {
        this.connection = connection;
        this.clientMap = new HashMap<>();
    }

    public Connection getConnection() {
        return connection;
    }

    public IRabbitMQClient getClient(String key) throws IOException {
        final IRabbitMQClient client;
        if (this.clientMap.containsKey(key)) {
            client = this.clientMap.get(key);
        }
        else {
            client = createNewClient(key);
        }

        return client;
    }

    public void closeClient(String key) throws IOException, TimeoutException {
        if (this.clientMap.containsKey(key)) {
            this.clientMap.get(key).closeChannel();
            this.clientMap.remove(key);
        }
    }

    private IRabbitMQClient createNewClient(String key) throws IOException {
        final Channel channel = this.connection.createChannel();
        final IRabbitMQClient client = new RabbitMQClient(channel);
        this.clientMap.put(key, client);

        return client;
    }
}

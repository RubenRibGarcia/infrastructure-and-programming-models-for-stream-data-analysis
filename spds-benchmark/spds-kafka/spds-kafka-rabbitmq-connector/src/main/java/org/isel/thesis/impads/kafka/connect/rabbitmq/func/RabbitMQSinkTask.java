package org.isel.thesis.impads.kafka.connect.rabbitmq.func;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.isel.thesis.impads.kafka.connect.rabbitmq.conf.RabbitMQConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMQSinkTask extends SinkTask {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQSinkTask.class);

    private RabbitMQConfiguration config;

    private Connection connection;
    private Channel channel;

    @Override
    public String version() {
        return new RabbitMQSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> mapConfig) {
        try {
            this.config = RabbitMQConfiguration.load(mapConfig);
            this.connection = RabbitMQConnectionFactory.createNewConnection(this.config);
            this.channel = this.connection.createChannel();

        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        collection.forEach(sinkRecord -> {
            String jsonString = (String)sinkRecord.value();
            try {
                this.channel.basicPublish(""
                        , config.getRabbitMQQueue()
                        , null
                        , jsonString.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    @Override
    public void stop() {
        LOG.info("Closing RabbitMQ Connector");
        try {
            this.channel.close();
            this.connection.close();
        } catch (TimeoutException | IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

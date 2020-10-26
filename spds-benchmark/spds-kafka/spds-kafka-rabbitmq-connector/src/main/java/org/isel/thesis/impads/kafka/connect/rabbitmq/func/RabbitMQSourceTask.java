package org.isel.thesis.impads.kafka.connect.rabbitmq.func;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.impl.AMQImpl;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.isel.thesis.impads.kafka.connect.rabbitmq.conf.RabbitMQConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQSourceTask extends SourceTask {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQSourceTask.class);

    private RabbitMQConfiguration config;

    private Connection connection;
    private Channel channel;

    private QueueingConsumer consumer;

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

            channel.queueDeclare(this.config.getRabbitMQQueue(), true, false, false, null);
            this.consumer = new QueueingConsumer(channel);

            this.channel.basicConsume(this.config.getRabbitMQQueue()
                    , true
                    , consumer);

        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new LinkedList<>();

        Optional<Delivery> delivery = Optional.ofNullable(this.consumer.nextDelivery());

        delivery.ifPresent(d -> {
            SourceRecord record = new SourceRecord(null
                    , Collections.singletonMap("deliveryTag", delivery.get().getEnvelope().getDeliveryTag())
                    , this.config.getKafkaTopic()
                    , Schema.STRING_SCHEMA
                    , new String(delivery.get().getBody()));

            records.add(record);
        });

        return records;
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

    @Override
    public void commitRecord(SourceRecord record, RecordMetadata recordMetadata) throws InterruptedException {
        Optional.ofNullable(record).ifPresent(rec -> {
            LOG.debug("Committing record on RabbitMQ");
            long deliveryTag = Long.parseLong(record.sourceOffset().get("deliveryTag").toString());
            LOG.debug("Committing record with delivery tag {} on RabbitMQ", deliveryTag);
        });
    }
}

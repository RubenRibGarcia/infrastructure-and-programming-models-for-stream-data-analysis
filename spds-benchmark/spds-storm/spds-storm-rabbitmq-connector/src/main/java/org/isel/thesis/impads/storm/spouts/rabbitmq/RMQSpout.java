package org.isel.thesis.impads.storm.spouts.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.ITupleProducer;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.spouts.rabbitmq.func.RabbitMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class RMQSpout implements IRichSpout {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RMQSpout.class);

    private SpoutOutputCollector collector;
    private TopologyContext context;

    private final RabbitMQConfiguration config;
    protected transient Connection connection;
    protected transient Channel channel;
    protected transient QueueingConsumer consumer;

    protected final String queueName;
    private final transient boolean autoAcknowledge;
    private final ITupleProducer tupleProducer;

    private RMQSpout(final RabbitMQConfiguration config
            , final String queueName
            , boolean autoAcknowledge
            , final ITupleProducer tupleProducer) {
        this.config = config;
        this.queueName = queueName;
        this.autoAcknowledge = autoAcknowledge;
        this.tupleProducer = tupleProducer;
    }

    public static RMQSpout newRabbitMQSpout(final RabbitMQConfiguration config
            , final String queueName
            , final boolean autoAcknowledge
            , final ITupleProducer tupleProducer) {
        return new RMQSpout(config, queueName, autoAcknowledge, tupleProducer);
    }

    @Override
    public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        LOG.debug("Opening RabbitMQ Spout");
        LOG.debug("Creating RabbitMQ Connection at {}:{}", config.getHost(), config.getPort());
        try {

            this.connection = RabbitMQConnectionFactory.createNewConnection(this.config);
            this.channel = connection.createChannel();

            Util.declareQueueDefaults(channel, queueName);
            consumer = new QueueingConsumer(channel);

            this.channel.basicConsume(queueName
                    , autoAcknowledge
                    , consumer);

            this.collector = spoutOutputCollector;
            this.context = topologyContext;
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        LOG.debug("Closing RabbitMQ Spout");
        try {
            if (consumer != null && channel != null) {
                channel.basicCancel(consumer.getConsumerTag());
            }

            if (channel != null) {
                this.channel.close();
            }

            if (connection != null) {
                this.connection.close();
            }
        } catch (IOException | TimeoutException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void activate() {
        LOG.info("Activating RabbitMQ Spout");
    }

    @Override
    public void deactivate() {
        LOG.info("Deactivating RabbitMQ Spout");
    }

    @Override
    public void nextTuple() {
        try {
            Optional.ofNullable(consumer.nextDelivery())
                    .ifPresent(this::doNextTuple);
        }
        catch(Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void doNextTuple(Delivery delivery) {
        Values tuple = tupleProducer.toTuple(delivery.getBody());
        if (!autoAcknowledge) {
            collector.emit(tuple, delivery.getEnvelope().getDeliveryTag());
        }
        else {
            collector.emit(tuple);
        }
    }

    @Override
    public void ack(Object o) {
        try {
            if (!autoAcknowledge) {
                long deliveryTag = Long.parseLong(o.toString());
                channel.basicAck(deliveryTag, false);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fail(Object o) {
        try {
            if (!autoAcknowledge) {
                long deliveryTag = Long.parseLong(o.toString());
                channel.basicNack(deliveryTag, false, true);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        this.tupleProducer.declareOutputFields(outputFieldsDeclarer, Optional.empty());
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}

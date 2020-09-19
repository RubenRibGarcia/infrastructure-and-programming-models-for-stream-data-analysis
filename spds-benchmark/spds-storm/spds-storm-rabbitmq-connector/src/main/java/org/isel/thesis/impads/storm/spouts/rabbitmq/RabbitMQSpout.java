package org.isel.thesis.impads.storm.spouts.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import org.apache.storm.shade.com.google.common.base.Optional;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.IRabbitMQQueue;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.ITupleProducer;
import org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.spouts.rabbitmq.func.RabbitMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQSpout implements IRichSpout {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQSpout.class);

    private SpoutOutputCollector collector;
    private TopologyContext context;

    private final RabbitMQConfiguration config;
    private final IRabbitMQQueue rabbitMQQueue;
    private final boolean autoAcknowledge;
    private final ITupleProducer tupleProducer;

    private final BlockingQueue<Delivery> queue;

    private transient Connection connection;
    private transient Channel channel;

    private final Optional<String> streamId;

    private RabbitMQSpout(final RabbitMQConfiguration config
            , final IRabbitMQQueue rabbitMQQueue
            , boolean autoAcknowledge
            , final ITupleProducer tupleProducer
            , final Optional<String> streamId) {
        this.config = config;
        this.rabbitMQQueue = rabbitMQQueue;
        this.autoAcknowledge = autoAcknowledge;
        this.tupleProducer = tupleProducer;
        this.streamId = streamId;
        this.queue = new LinkedBlockingQueue<>();
    }

    public static RabbitMQSpout newRabbitMQSpout(final RabbitMQConfiguration config
            , final IRabbitMQQueue rabbitMQQueue
            , final boolean autoAcknowledge
            , final ITupleProducer tupleProducer
            , final String streamId) {
        return new RabbitMQSpout(config, rabbitMQQueue, autoAcknowledge, tupleProducer, Optional.fromNullable(streamId));
    }

    public static RabbitMQSpout newRabbitMQSpout(final RabbitMQConfiguration config
            , final IRabbitMQQueue rabbitMQQueue
            , final boolean autoAcknowledge
            , final ITupleProducer tupleProducer) {
        return new RabbitMQSpout(config, rabbitMQQueue, autoAcknowledge, tupleProducer, Optional.absent());
    }

    @Override
    public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        LOG.debug("Opening RabbitMQ Spout");
        LOG.debug("Creating RabbitMQ Connection at {}:{}", config.getRabbitMQHost(), config.getRabbitMQPort());
        try {
            final Connection connection = RabbitMQConnectionFactory.createNewConnection(this.config);

            this.connection = connection;
            this.channel = connection.createChannel();

            this.channel.basicConsume(rabbitMQQueue.getQueueName()
                    , autoAcknowledge
                    , deliverCallback()
                    , consumerTag -> {});

            this.collector = spoutOutputCollector;
            this.context = topologyContext;
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private DeliverCallback deliverCallback() {
        return (consumerTag, delivery) -> queue.add(delivery);
    }

    @Override
    public void close() {
        LOG.debug("Closing RabbitMQ Spout");
        try {
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
        Optional<Delivery> delivery = Optional.fromNullable(queue.poll());
        if (delivery.isPresent()) {
            Values tuple = tupleProducer.toTuple(delivery.get().getBody());
            if (streamId.isPresent()) {
                collector.emit(streamId.get(), tuple, delivery.get().getEnvelope().getDeliveryTag());
            }
            else {
                collector.emit(tuple, delivery.get().getEnvelope().getDeliveryTag());
            }

        }
    }

    @Override
    public void ack(Object o) {
        if (!autoAcknowledge) {
            try {
                long deliveryTag = Long.parseLong(o.toString());
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void fail(Object o) {
        if (!autoAcknowledge) {
            try {
                long deliveryTag = Long.parseLong(o.toString());
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        this.tupleProducer.declareOutputFields(outputFieldsDeclarer, streamId);
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}

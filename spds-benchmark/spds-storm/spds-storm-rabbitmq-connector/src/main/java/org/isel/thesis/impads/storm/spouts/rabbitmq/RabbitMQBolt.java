package org.isel.thesis.impads.storm.spouts.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.IRabbitMQQueue;
import org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.spouts.rabbitmq.func.RabbitMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMQBolt implements IRichBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQBolt.class);

    private OutputCollector outputCollector;
    private TopologyContext context;

    private final RabbitMQConfiguration config;
    private final IRabbitMQQueue rabbitMQQueue;

    private transient Connection connection;
    private transient Channel channel;

    private RabbitMQBolt(final RabbitMQConfiguration config
            , final IRabbitMQQueue rabbitMQQueue) {

        this.config = config;
        this.rabbitMQQueue = rabbitMQQueue;
    }

    public static RabbitMQBolt newRabbitMQBolt(final RabbitMQConfiguration config
            , final IRabbitMQQueue rabbitMQQueue) {
        return new RabbitMQBolt(config, rabbitMQQueue);
    }

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        LOG.info("Opening RabbitMQ Bolt");
        LOG.info("Creating RabbitMQ Connection at {}:{}", config.getRabbitMQHost(), config.getRabbitMQPort());
        try {
            final Connection connection = RabbitMQConnectionFactory.createNewConnection(this.config);

            this.connection = connection;
            this.channel = connection.createChannel();

            this.outputCollector = outputCollector;
            this.context = topologyContext;
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        System.out.println(tuple);
    }

    @Override
    public void cleanup() {
        LOG.info("Closing RabbitMQ Spout");
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
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}

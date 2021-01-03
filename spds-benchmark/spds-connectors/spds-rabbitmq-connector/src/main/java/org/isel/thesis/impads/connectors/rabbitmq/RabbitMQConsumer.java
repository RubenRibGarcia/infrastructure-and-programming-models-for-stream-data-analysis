package org.isel.thesis.impads.connectors.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.isel.thesis.impads.connectors.rabbitmq.common.RabbitMQConfiguration;
import org.isel.thesis.impads.io.OpenCloseable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RabbitMQConsumer<O> implements OpenCloseable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(RabbitMQConsumer.class);

	private final RabbitMQConfiguration rmqConnectionConfig;
	protected final String queueName;
	private final boolean usesCorrelationId;
//	protected DeserializationSchema<O> schema;

	protected transient Connection connection;
	protected transient Channel channel;
	protected transient QueueingConsumer consumer;

	protected transient boolean autoAck;

	private transient volatile boolean running;

	public RabbitMQConsumer(RabbitMQConfiguration rmqConnectionConfig
			, String queueName
			/*, DeserializationSchema<O> deserializationSchema */) {
		this(rmqConnectionConfig, queueName, false);
	}

	public RabbitMQConsumer(RabbitMQConfiguration rmqConnectionConfig
			, String queueName
			, boolean usesCorrelationId
			/*, DeserializationSchema<O> deserializationSchema*/) {

		this.rmqConnectionConfig = rmqConnectionConfig;
		this.queueName = queueName;
		this.usesCorrelationId = usesCorrelationId;
//		this.schema = deserializationSchema;
	}

	protected ConnectionFactory setupConnectionFactory() throws Exception {
		return rmqConnectionConfig.getConnectionFactory();
	}

	protected Connection setupConnection() throws Exception {
		return setupConnectionFactory().newConnection();
	}

	private Channel setupChannel(Connection connection) throws Exception {
		Channel chan = connection.createChannel();
//		if (rmqConnectionConfig.getPrefetchCount().isPresent()) {
//			// set the global flag for the entire channel, though shouldn't make a difference
//			// since there is only one consumer, and each parallel instance of the source will
//			// create a new connection (and channel)
//			chan.basicQos(rmqConnectionConfig.getPrefetchCount().get(), true);
//		}
		return chan;
	}

	protected void setupQueue() throws IOException {
		Util.declareQueueDefaults(channel, queueName);
	}

	@Override
	public void open() throws Exception {
		try {
			connection = setupConnection();
			channel = setupChannel(connection);
			if (channel == null) {
				throw new RuntimeException("None of RabbitMQ channels are available");
			}
			setupQueue();
			consumer = new QueueingConsumer(channel);

			channel.basicConsume(queueName, autoAck, consumer);

		} catch (IOException e) {
			throw new RuntimeException("Cannot create RMQ connection with " + queueName + " at "
					+ rmqConnectionConfig.getHost(), e);
		}
		running = true;
	}

	@Override
	public void close() throws Exception {
		try {
			if (consumer != null && channel != null) {
				channel.basicCancel(consumer.getConsumerTag());
			}
		} catch (IOException e) {
			throw new RuntimeException("Error while cancelling RMQ consumer on " + queueName
				+ " at " + rmqConnectionConfig.getHost(), e);
		}

		try {
			if (channel != null) {
				channel.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error while closing RMQ channel with " + queueName
				+ " at " + rmqConnectionConfig.getHost(), e);
		}

		try {
			if (connection != null) {
				connection.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error while closing RMQ connection with " + queueName
				+ " at " + rmqConnectionConfig.getHost(), e);
		}
	}

//	@Override
//	public void run(SourceContext<OUT> ctx) throws Exception {
//		final RMQCollector collector = new RMQCollector(ctx);
//		while (running) {
//			Delivery delivery = consumer.nextDelivery();
//
//			synchronized (ctx.getCheckpointLock()) {
//				if (!autoAck) {
//					final long deliveryTag = delivery.getEnvelope().getDeliveryTag();
//					if (usesCorrelationId) {
//						final String correlationId = delivery.getProperties().getCorrelationId();
//						Preconditions.checkNotNull(correlationId, "RabbitMQ source was instantiated " +
//							"with usesCorrelationId set to true but a message was received with " +
//							"correlation id set to null!");
//						if (!addId(correlationId)) {
//							// we have already processed this message
//							continue;
//						}
//					}
//					sessionIds.add(deliveryTag);
//				}
//
//				schema.deserialize(delivery.getBody(), collector);
//				if (collector.isEndOfStreamSignalled()) {
//					this.running = false;
//					return;
//				}
//			}
//		}
//	}
}

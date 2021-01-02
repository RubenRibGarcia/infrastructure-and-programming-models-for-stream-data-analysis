package org.isel.thesis.impads.connectors.rabbitmq;

import com.rabbitmq.client.Channel;

import java.io.IOException;

class Util {

	static void declareQueueDefaults(Channel channel, String queueName) throws IOException {
		channel.queueDeclare(queueName, true, false, false, null);
	}
}

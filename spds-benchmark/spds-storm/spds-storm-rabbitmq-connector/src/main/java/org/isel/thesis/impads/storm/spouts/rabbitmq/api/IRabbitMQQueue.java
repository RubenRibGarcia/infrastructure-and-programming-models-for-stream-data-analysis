package org.isel.thesis.impads.storm.spouts.rabbitmq.api;

import java.io.Serializable;

public interface IRabbitMQQueue extends Serializable {

    String getQueueName();

    final class RabbitMQQueueNaming {
        public static IRabbitMQQueue withName(String queueName) {
            return () -> queueName;
        }
    }
}

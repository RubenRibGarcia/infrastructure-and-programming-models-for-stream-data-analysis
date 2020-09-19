package org.isel.thesis.impads.giragen.connector.rabbitmq.api;

public interface IRabbitMQQueue {

    String getQueueName();

    final class RabbitMQQueueNaming {
        public static IRabbitMQQueue withName(String queueName) {
            return () -> queueName;
        }
    }
}

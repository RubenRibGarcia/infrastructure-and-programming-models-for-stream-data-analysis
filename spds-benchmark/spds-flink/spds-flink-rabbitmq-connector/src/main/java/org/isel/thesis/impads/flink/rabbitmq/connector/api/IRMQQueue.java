package org.isel.thesis.impads.flink.rabbitmq.connector.api;

public interface IRMQQueue {

    String getQueueName();

    final class RMQQueueNaming {
        public static IRMQQueue withName(String queueName) {
            return () -> queueName;
        }
    }
}

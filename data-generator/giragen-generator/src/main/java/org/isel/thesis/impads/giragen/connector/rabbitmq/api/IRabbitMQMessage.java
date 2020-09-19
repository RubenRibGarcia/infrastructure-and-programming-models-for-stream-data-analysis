package org.isel.thesis.impads.giragen.connector.rabbitmq.api;

public interface IRabbitMQMessage {

    String getMessage();

    class RabbitMQMessaging {
        public static IRabbitMQMessage withMessage(String message) {
            return () -> message;
        }
    }
}

package org.isel.thesis.impads.giragen.connector.rabbitmq.api;

@FunctionalInterface
public interface WithRabbitMQClientCallback<R> {

    R apply(IRabbitMQClient client);
}

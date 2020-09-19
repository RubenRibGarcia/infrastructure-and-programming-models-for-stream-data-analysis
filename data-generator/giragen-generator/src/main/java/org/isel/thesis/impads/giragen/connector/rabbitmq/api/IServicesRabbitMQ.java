package org.isel.thesis.impads.giragen.connector.rabbitmq.api;


public interface IServicesRabbitMQ {

    <R> R withRabbitMQClient(String key, WithRabbitMQClientCallback<R> fn);

    IRabbitMQClient createRabbitMQClient(String key);

    IRabbitMQClient createRabbitMQClient();
}

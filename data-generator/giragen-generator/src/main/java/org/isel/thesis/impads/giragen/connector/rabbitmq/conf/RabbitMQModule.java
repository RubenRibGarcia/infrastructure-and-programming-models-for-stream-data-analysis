package org.isel.thesis.impads.giragen.connector.rabbitmq.conf;

import dagger.Module;
import dagger.Provides;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQConnection;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IServicesRabbitMQ;
import org.isel.thesis.impads.giragen.connector.rabbitmq.func.RabbitMQConnectionFactory;
import org.isel.thesis.impads.giragen.connector.rabbitmq.func.ServicesRabbitMQ;

import javax.inject.Singleton;


@Module
public class RabbitMQModule {

    private final RabbitMQConfiguration config;

    public RabbitMQModule(RabbitMQConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    RabbitMQConfiguration provideRabbitMQConfigurationModule() {
        return this.config;
    }

    @Provides
    @Singleton
    IRabbitMQConnection proviceRabbitMQConnection(RabbitMQConfiguration config) {
        return RabbitMQConnectionFactory.createNewConnection(config).get();
    }

    @Provides
    @Singleton
    IServicesRabbitMQ provideServicesRabbitMQ(IRabbitMQConnection connection) {
        return new ServicesRabbitMQ(connection);
    }
}

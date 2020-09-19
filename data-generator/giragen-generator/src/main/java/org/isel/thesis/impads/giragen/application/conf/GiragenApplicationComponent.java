package org.isel.thesis.impads.giragen.application.conf;

import dagger.Component;
import org.isel.thesis.impads.giragen.application.func.GiragenApplication;
import org.isel.thesis.impads.giragen.base.conf.JacksonModule;
import org.isel.thesis.impads.giragen.base.conf.TypesafeConfigModule;
import org.isel.thesis.impads.giragen.connector.rabbitmq.conf.RabbitMQModule;
import org.isel.thesis.impads.giragen.generator.conf.GeneratorModule;
import org.isel.thesis.impads.giragen.metrics.conf.MetricsModule;

import javax.inject.Singleton;

@Component(modules = {TypesafeConfigModule.class
        , JacksonModule.class
        , RabbitMQModule.class
        , MetricsModule.class
        , GeneratorModule.class})
@Singleton
public interface GiragenApplicationComponent {
    GiragenApplication app();
}

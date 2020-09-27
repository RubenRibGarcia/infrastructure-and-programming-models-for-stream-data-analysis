package org.isel.thesis.impads.giragen;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.isel.thesis.impads.giragen.application.conf.DaggerGiragenApplicationComponent;
import org.isel.thesis.impads.giragen.application.func.GiragenApplication;
import org.isel.thesis.impads.giragen.base.conf.JacksonModule;
import org.isel.thesis.impads.giragen.base.conf.TypesafeConfigModule;
import org.isel.thesis.impads.giragen.connector.rabbitmq.conf.RabbitMQConfiguration;
import org.isel.thesis.impads.giragen.connector.rabbitmq.conf.RabbitMQModule;
import org.isel.thesis.impads.giragen.generator.conf.GeneratorModule;
import org.isel.thesis.impads.giragen.metrics.conf.MetricsModule;

public class MainGiragenApplication
{
    public static void main( String[] args )
    {
        Config conf = ConfigFactory.load();

        try {
            GiragenApplication app = DaggerGiragenApplicationComponent.builder()
                    .typesafeConfigModule(TypesafeConfigModule.install(conf))
                    .jacksonModule(JacksonModule.install())
                    .metricsModule(MetricsModule.install())
                    .generatorModule(GeneratorModule.install())
                    .rabbitMQModule(new RabbitMQModule(RabbitMQConfiguration.load(conf)))
                    .build()
                    .app();

            app.run();
        } catch(Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

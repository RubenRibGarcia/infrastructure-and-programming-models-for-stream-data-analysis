package org.isel.thesis.impads.kafka.connect.rabbitmq.func;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.isel.thesis.impads.kafka.connect.rabbitmq.conf.RabbitMQConfigurationFields;
import org.isel.thesis.impads.kafka.connect.rabbitmq.conf.RabbitMQSourceConnectorConfiguration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RabbitMQSourceConnector extends SourceConnector {

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(RabbitMQConfigurationFields.KAFKA_TOPIC, Type.STRING, Importance.HIGH,  "Kafka Topic")
            .define(RabbitMQConfigurationFields.RABBITMQ_USERNAME, Type.STRING, Importance.HIGH, "Rabbit MQ Username")
            .define(RabbitMQConfigurationFields.RABBITMQ_PASSWORD, Type.STRING, Importance.HIGH, "Rabbit MQ Password")
            .define(RabbitMQConfigurationFields.RABBITMQ_HOST, Type.STRING, Importance.HIGH, "Rabbit MQ Host")
            .define(RabbitMQConfigurationFields.RABBITMQ_PORT, Type.INT, Importance.HIGH, "Rabbit MQ Port")
            .define(RabbitMQConfigurationFields.RABBITMQ_QUEUE, Type.STRING, Importance.HIGH, "Rabbit MQ Queue");

    private RabbitMQSourceConnectorConfiguration conf;

    @Override
    public void start(Map<String, String> map) {
        AbstractConfig parsedConfig = new AbstractConfig(CONFIG_DEF, map);
        this.conf = RabbitMQSourceConnectorConfiguration.load(parsedConfig);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return RabbitMQSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        List<Map<String, String>> configs = new LinkedList<>();

        Map<String, String> config = new HashMap<>();
        config.put(RabbitMQConfigurationFields.KAFKA_TOPIC, this.conf.getKafkaTopic());
        config.put(RabbitMQConfigurationFields.RABBITMQ_HOST, this.conf.getRabbitMQHost());
        config.put(RabbitMQConfigurationFields.RABBITMQ_PORT, String.valueOf(this.conf.getRabbitMQPort()));
        config.put(RabbitMQConfigurationFields.RABBITMQ_USERNAME, this.conf.getRabbitMQUsername());
        config.put(RabbitMQConfigurationFields.RABBITMQ_PASSWORD, this.conf.getRabbitMQPassword());
        config.put(RabbitMQConfigurationFields.RABBITMQ_QUEUE, this.conf.getRabbitMQQueue());

        configs.add(config);
        return configs;
    }

    @Override
    public void stop() {
        //Nothing to do here
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }


}

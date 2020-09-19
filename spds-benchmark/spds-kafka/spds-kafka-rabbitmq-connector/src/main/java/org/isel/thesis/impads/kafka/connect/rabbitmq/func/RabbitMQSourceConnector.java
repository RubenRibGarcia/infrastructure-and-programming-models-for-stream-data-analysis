package org.isel.thesis.impads.kafka.connect.rabbitmq.func;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.isel.thesis.impads.kafka.connect.rabbitmq.conf.RabbitMQSourceConnectorConfiguration;
import org.isel.thesis.impads.kafka.connect.rabbitmq.conf.RabbitMQSourceConnectorConfiguration.RabbitMQSourceConnectorConfigurationFields;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RabbitMQSourceConnector extends SourceConnector {

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(RabbitMQSourceConnectorConfigurationFields.KAFKA_TOPIC, Type.STRING, Importance.HIGH,  "Kafka Topic")
            .define(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_USERNAME, Type.STRING, Importance.HIGH, "Rabbit MQ Username")
            .define(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_PASSWORD, Type.STRING, Importance.HIGH, "Rabbit MQ Password")
            .define(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_HOST, Type.STRING, Importance.HIGH, "Rabbit MQ Host")
            .define(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_PORT, Type.INT, Importance.HIGH, "Rabbit MQ Port")
            .define(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_QUEUE, Type.STRING, Importance.HIGH, "Rabbit MQ Queue");

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
        config.put(RabbitMQSourceConnectorConfigurationFields.KAFKA_TOPIC, this.conf.getKafkaTopic());
        config.put(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_HOST, this.conf.getRabbitMQHost());
        config.put(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_PORT, String.valueOf(this.conf.getRabbitMQPort()));
        config.put(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_USERNAME, this.conf.getRabbitMQUsername());
        config.put(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_PASSWORD, this.conf.getRabbitMQPassword());
        config.put(RabbitMQSourceConnectorConfigurationFields.RABBITMQ_QUEUE, this.conf.getRabbitMQQueue());

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

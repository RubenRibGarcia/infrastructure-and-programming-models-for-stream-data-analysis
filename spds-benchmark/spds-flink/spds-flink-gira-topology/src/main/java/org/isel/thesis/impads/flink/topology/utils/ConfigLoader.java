package org.isel.thesis.impads.flink.topology.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    private ConfigLoader() { }

    public static Config loadFromParameterTool(ParameterTool parameters) {

        String configFilePath = parameters.get("config.file.path");

        logger.info("Config File Path: {}", configFilePath);

        final File file = new File(configFilePath);
        final Config conf = ConfigFactory.parseFile(file);

        return conf;
    }
}

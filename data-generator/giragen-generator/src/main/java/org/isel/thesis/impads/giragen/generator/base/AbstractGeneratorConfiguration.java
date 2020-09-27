package org.isel.thesis.impads.giragen.generator.base;

import com.typesafe.config.Config;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.isel.thesis.impads.giragen.generator.base.GeneratorConfigurationModuleFields.buildGeneratorDataFilePathProperty;
import static org.isel.thesis.impads.giragen.generator.base.GeneratorConfigurationModuleFields.buildGeneratorEnabled;
import static org.isel.thesis.impads.giragen.generator.base.GeneratorConfigurationModuleFields.buildGeneratorQueueName;
import static org.isel.thesis.impads.giragen.generator.base.GeneratorConfigurationModuleFields.buildGeneratorThreadsProperty;

public abstract class AbstractGeneratorConfiguration {

    protected final Config config;
    protected final String suffix;

    protected AbstractGeneratorConfiguration(Config config, String suffix) {
        this.config = config;
        this.suffix = suffix;
    }

    @Deprecated
    public int getGeneratorThreads() {
        return config.getInt(buildGeneratorThreadsProperty(suffix));
    }

    public Path getGeneratorDataFilePath() {
        return Paths.get(config.getString(buildGeneratorDataFilePathProperty(suffix)));
    }

    public String getGeneratorQueueName() {
        return config.getString(buildGeneratorQueueName(suffix));
    }

    public boolean isGeneratorEnabled() {
        return config.getBoolean(buildGeneratorEnabled(suffix));
    }
}

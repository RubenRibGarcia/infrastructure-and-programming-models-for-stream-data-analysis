package org.isel.thesis.impads.giragen.generator.conf;

import com.typesafe.config.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GeneratorConfiguration {

    private static final String GENERATOR_THREAD_POOL_SIZE = "generator.thread_pool.size";

    private final Config config;

    @Inject
    public GeneratorConfiguration(Config config) {
        this.config = config;
    }

    public int getGeneratorThreadPoolSize() {
        return config.getInt(GENERATOR_THREAD_POOL_SIZE);
    }

}

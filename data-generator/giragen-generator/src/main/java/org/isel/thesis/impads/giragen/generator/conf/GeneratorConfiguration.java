package org.isel.thesis.impads.giragen.generator.conf;

import com.typesafe.config.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GeneratorConfiguration {

    private static final String GENERATOR_THROUGHPUT_EVENT_PER_SECOND = "generator.throughput.events_per_second";

    private final Config config;

    @Inject
    public GeneratorConfiguration(Config config) {
        this.config = config;
    }

    public long getGeneratorThroughtputEventsPerSecond() {
        return config.getLong(GENERATOR_THROUGHPUT_EVENT_PER_SECOND);
    }

}

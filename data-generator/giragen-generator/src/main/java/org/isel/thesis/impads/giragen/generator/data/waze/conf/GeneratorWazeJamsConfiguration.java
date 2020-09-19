package org.isel.thesis.impads.giragen.generator.data.waze.conf;

import com.typesafe.config.Config;
import org.isel.thesis.impads.giragen.generator.base.AbstractGeneratorConfiguration;

import javax.inject.Inject;

public class GeneratorWazeJamsConfiguration extends AbstractGeneratorConfiguration {

    private static final String WAZE_JAMS_SUFFIX = "waze.jams";

    @Inject
    public GeneratorWazeJamsConfiguration(Config config) {
        super(config, WAZE_JAMS_SUFFIX);
    }
}

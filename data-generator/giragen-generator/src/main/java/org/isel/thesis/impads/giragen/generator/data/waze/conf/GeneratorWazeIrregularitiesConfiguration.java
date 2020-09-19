package org.isel.thesis.impads.giragen.generator.data.waze.conf;

import com.typesafe.config.Config;
import org.isel.thesis.impads.giragen.generator.base.AbstractGeneratorConfiguration;

import javax.inject.Inject;

public class GeneratorWazeIrregularitiesConfiguration extends AbstractGeneratorConfiguration {

    private static final String WAZE_IRREGULARITIES_SUFFIX = "waze.irregularities";

    @Inject
    public GeneratorWazeIrregularitiesConfiguration(Config config) {
        super(config, WAZE_IRREGULARITIES_SUFFIX);
    }
}

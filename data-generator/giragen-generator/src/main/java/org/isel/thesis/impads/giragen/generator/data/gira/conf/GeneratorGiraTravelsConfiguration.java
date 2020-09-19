package org.isel.thesis.impads.giragen.generator.data.gira.conf;

import com.typesafe.config.Config;
import org.isel.thesis.impads.giragen.generator.base.AbstractGeneratorConfiguration;

import javax.inject.Inject;

public class GeneratorGiraTravelsConfiguration extends AbstractGeneratorConfiguration {

    private static final String GIRA_TRAVEL_SUFFIX = "gira_travels";


    @Inject
    public GeneratorGiraTravelsConfiguration(Config config) {
        super(config, GIRA_TRAVEL_SUFFIX);
    }
}

package org.isel.thesis.impads.giragen.application.func;

import org.isel.thesis.impads.giragen.generator.api.IGeneratorSubmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class GiragenApplication {

    private static final Logger logger = LoggerFactory.getLogger(GiragenApplication.class);

    private IGeneratorSubmitter generatorExecutor;

    @Inject
    public GiragenApplication(IGeneratorSubmitter generatorExecutor) {
        this.generatorExecutor = generatorExecutor;
    }

    public void run() {
        logger.info("Initializing Generators");
        this.generatorExecutor.submitGenerators();
    }
}

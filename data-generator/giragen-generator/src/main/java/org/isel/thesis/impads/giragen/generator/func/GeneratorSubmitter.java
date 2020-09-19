package org.isel.thesis.impads.giragen.generator.func;

import org.isel.thesis.impads.giragen.generator.api.IGeneratorSubmitter;
import org.isel.thesis.impads.giragen.generator.data.gira.generator.GeneratorGiraTravelsExecutor;
import org.isel.thesis.impads.giragen.generator.data.waze.generator.GeneratorWazeIrregularitiesExecutor;
import org.isel.thesis.impads.giragen.generator.data.waze.generator.GeneratorWazeJamsExecutor;

import javax.inject.Inject;

public class GeneratorSubmitter implements IGeneratorSubmitter {

    private final GeneratorGiraTravelsExecutor generatorGiraTravelsExecutor;
    private final GeneratorWazeIrregularitiesExecutor generatorWazeIrregularitiesExecutor;
    private final GeneratorWazeJamsExecutor generatorWazeJamsExecutor;

    @Inject
    public GeneratorSubmitter(GeneratorGiraTravelsExecutor generatorGiraTravelsExecutor
            , GeneratorWazeIrregularitiesExecutor generatorWazeIrregularitiesExecutor
            , GeneratorWazeJamsExecutor generatorWazeJamsExecutor) {
        this.generatorGiraTravelsExecutor = generatorGiraTravelsExecutor;
        this.generatorWazeIrregularitiesExecutor = generatorWazeIrregularitiesExecutor;
        this.generatorWazeJamsExecutor = generatorWazeJamsExecutor;
    }

    @Override
    public void submitGenerators() {
        this.generatorGiraTravelsExecutor.submitGenerator();
        this.generatorWazeIrregularitiesExecutor.submitGenerator();
        this.generatorWazeJamsExecutor.submitGenerator();
    }
}

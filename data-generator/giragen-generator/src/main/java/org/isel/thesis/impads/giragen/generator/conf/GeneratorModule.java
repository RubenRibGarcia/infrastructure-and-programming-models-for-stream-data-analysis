package org.isel.thesis.impads.giragen.generator.conf;

import dagger.Module;
import dagger.Provides;
import org.isel.thesis.impads.giragen.generator.api.IGeneratorSubmitter;
import org.isel.thesis.impads.giragen.generator.data.gira.generator.GeneratorGiraTravelsExecutor;
import org.isel.thesis.impads.giragen.generator.data.waze.generator.GeneratorWazeIrregularitiesExecutor;
import org.isel.thesis.impads.giragen.generator.data.waze.generator.GeneratorWazeJamsExecutor;
import org.isel.thesis.impads.giragen.generator.func.GeneratorSubmitter;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Module
public class GeneratorModule {

    private GeneratorModule() { }

    public static GeneratorModule install() {
        return new GeneratorModule();
    }

    @Provides
    @Singleton
    IGeneratorSubmitter provideGeneratorExecutor(GeneratorGiraTravelsExecutor generatorGiraTravelsExecutor
            , GeneratorWazeIrregularitiesExecutor generatorWazeIrregularitiesExecutor
            , GeneratorWazeJamsExecutor generatorWazeJamsExecutor) {
        return new GeneratorSubmitter(generatorGiraTravelsExecutor
                , generatorWazeIrregularitiesExecutor
                , generatorWazeJamsExecutor);
    }

    @Provides
    @Singleton
    ExecutorService providesGeneratorExecutorService(GeneratorConfiguration configuration) {
        return Executors.newFixedThreadPool(configuration.getGeneratorThreadPoolSize());
    }
}

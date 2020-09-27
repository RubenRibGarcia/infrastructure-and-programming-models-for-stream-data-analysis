package org.isel.thesis.impads.giragen.generator.conf;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import dagger.Module;
import dagger.Provides;
import org.isel.thesis.impads.giragen.generator.api.IGeneratorSubmitter;
import org.isel.thesis.impads.giragen.generator.data.gira.generator.GeneratorGiraTravelsExecutor;
import org.isel.thesis.impads.giragen.generator.data.waze.generator.GeneratorWazeIrregularitiesExecutor;
import org.isel.thesis.impads.giragen.generator.data.waze.generator.GeneratorWazeJamsExecutor;
import org.isel.thesis.impads.giragen.generator.func.GeneratorSubmitter;
import org.isel.thesis.impads.giragen.metrics.api.IServicesMetrics;
import org.isel.thesis.impads.giragen.metrics.func.ServicesMetrics;
import org.isel.thesis.impads.giragen.metrics.func.ServicesMetrics.MetricsCounter;

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
    RateLimiter provideRateLimiter(GeneratorConfiguration configuration) {
        Preconditions.checkArgument(configuration.getGeneratorThroughtputEventsPerSecond() > 0
                , "Property generator.throughput.events_per_second must be greater than 0");
        return RateLimiter.create(configuration.getGeneratorThroughtputEventsPerSecond());
    }

    @Provides
    @Singleton
    MetricsCounter provideGeneratorMetricsCounter(IServicesMetrics servicesMetrics) {
        return servicesMetrics.withMetricCounter("giragen.messages.generated.count");
    }
}

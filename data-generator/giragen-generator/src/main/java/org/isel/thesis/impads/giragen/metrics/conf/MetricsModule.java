package org.isel.thesis.impads.giragen.metrics.conf;

import dagger.Module;
import dagger.Provides;
import io.micrometer.core.instrument.Clock;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.isel.thesis.impads.giragen.metrics.api.IMetrics;
import org.isel.thesis.impads.giragen.metrics.api.IServicesMetrics;
import org.isel.thesis.impads.giragen.metrics.func.ServicesMetrics;
import org.isel.thesis.impads.giragen.metrics.statsd.MetricsStatsD;
import org.isel.thesis.impads.giragen.metrics.statsd.TelegrafStatsD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Module
public class MetricsModule {

    private static final Logger logger = LoggerFactory.getLogger(MetricsModule.class);

    private MetricsModule() { }

    public static MetricsModule install() {
        return new MetricsModule();
    }

    @Provides
    @Singleton
    IMetrics providesMetrics(MetricsModuleConfiguration config) {
        final IMetrics metrics;
        switch (config.getMetricStatsDAgent()) {
            case TELEGRAF:
                logger.info("[MetricsModule] Using Telegraf metrics module");
                metrics = new MetricsStatsD(new StatsdMeterRegistry(new TelegrafStatsD(config), Clock.SYSTEM));
                break;
            case NONE:
            default:
                metrics = null;
                break;
        }

        return metrics;
    }

    @Provides
    @Singleton
    IServicesMetrics providesServicesMetrics(IMetrics metrics) {
        return new ServicesMetrics(metrics);
    }

}

package org.isel.thesis.impads.giragen.base.conf;

import com.typesafe.config.Config;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class TypesafeConfigModule {

    private final Config config;

    private TypesafeConfigModule(Config config) {
        this.config = config;
    }

    public static TypesafeConfigModule install(Config config) {
        return new TypesafeConfigModule(config);
    }

    @Provides
    @Singleton
    Config providesConfig() {
        return config;
    }
}

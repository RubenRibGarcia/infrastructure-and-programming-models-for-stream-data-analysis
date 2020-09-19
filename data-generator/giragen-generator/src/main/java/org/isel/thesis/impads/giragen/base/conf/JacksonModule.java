package org.isel.thesis.impads.giragen.base.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class JacksonModule {

    private JacksonModule() { }

    public static JacksonModule install() {
        return new JacksonModule();
    }

    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }
}

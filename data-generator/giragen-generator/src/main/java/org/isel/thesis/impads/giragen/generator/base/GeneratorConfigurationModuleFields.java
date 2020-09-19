package org.isel.thesis.impads.giragen.generator.base;

public final class GeneratorConfigurationModuleFields {
    private static final String GENERATOR_PREFIX = "generator.";
    private static final String GENERATOR_DATA_PATH_SUFFIX = ".data_path";
    private static final String GENERATOR_WORKSPACE_PATH_SUFFIX = ".workspace_path";
    private static final String GENERATOR_QUEUE_NAME = ".queue_name";
    private static final String GENERATOR_THREADS_SUFFIX = ".threads";
    private static final String GENERATOR_ENABLED = ".enabled";

    public static String buildGeneratorThreadsProperty(String property) {
        return GENERATOR_PREFIX
                .concat(property)
                .concat(GENERATOR_THREADS_SUFFIX);
    }

    public static String buildGeneratorDataFilePathProperty(String property) {
        return GENERATOR_PREFIX
                .concat(property)
                .concat(GENERATOR_DATA_PATH_SUFFIX);
    }

    public static String buildGeneratorWorkspacePathProperty(String property) {
        return GENERATOR_PREFIX
                .concat(property)
                .concat(GENERATOR_WORKSPACE_PATH_SUFFIX);
    }

    public static String buildGeneratorQueueName(String property) {
        return GENERATOR_PREFIX
                .concat(property)
                .concat(GENERATOR_QUEUE_NAME);
    }

    public static String buildGeneratorEnabled(String property) {
        return GENERATOR_PREFIX
                .concat(property)
                .concat(GENERATOR_ENABLED);
    }
}

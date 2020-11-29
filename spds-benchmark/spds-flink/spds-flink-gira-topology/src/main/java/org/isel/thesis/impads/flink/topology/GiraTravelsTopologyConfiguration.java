package org.isel.thesis.impads.flink.topology;

public final class GiraTravelsTopologyConfiguration {

    private final int parallelism;

    private GiraTravelsTopologyConfiguration(int parallelism) {
        this.parallelism = parallelism;
    }

    public int getParallelism() {
        return parallelism;
    }

    public static GiraTravelsTopologyConfigurationBuilder builder() {
        return new GiraTravelsTopologyConfigurationBuilder();
    }

    public static final class GiraTravelsTopologyConfigurationBuilder {

        private static final int DEFAULT_PARALLELISM = 1;

        private int parallelism = DEFAULT_PARALLELISM;

        private GiraTravelsTopologyConfigurationBuilder() { }

        public GiraTravelsTopologyConfigurationBuilder withParallelism(int parallelism) {
            if (parallelism < 0) {
                throw new IllegalArgumentException("Parallelism must be greater than 0");
            }
            this.parallelism = parallelism;
            return this;
        }

        public GiraTravelsTopologyConfiguration build() {
            return new GiraTravelsTopologyConfiguration(parallelism);
        }
    }

    public static final class GiraTravelsTopologyConfigurationFields {
        private static final String TOPOLOGY_PREFIX = "topology.";
        public static final String TOPOLOGY_PARALLELISM = TOPOLOGY_PREFIX.concat("parallelism");
    }
}

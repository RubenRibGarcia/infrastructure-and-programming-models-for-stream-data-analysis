package org.isel.thesis.impads.kafka.stream.topology;


import org.isel.thesis.impads.kafka.stream.topology.phases.Phases;

public final class GiraTravelsTopologyConfiguration {

    private final int parallelism;
    private final Phases untilPhase;

    private GiraTravelsTopologyConfiguration(int parallelism, Phases untilPhase) {
        this.parallelism = parallelism;
        this.untilPhase = untilPhase;
    }

    public int getParallelism() {
        return parallelism;
    }

    public Phases getUntilPhase() {
        return untilPhase;
    }

    public static GiraTravelsTopologyConfigurationBuilder builder() {
        return new GiraTravelsTopologyConfigurationBuilder();
    }

    public static final class GiraTravelsTopologyConfigurationBuilder {

        private static final int DEFAULT_PARALLELISM = 1;

        private int parallelism = DEFAULT_PARALLELISM;
        private Phases untilPhase;

        private GiraTravelsTopologyConfigurationBuilder() { }

        public GiraTravelsTopologyConfigurationBuilder withParallelism(int parallelism) {
            if (parallelism < 0) {
                throw new IllegalArgumentException("Parallelism must be greater than 0");
            }
            this.parallelism = parallelism;
            return this;
        }

        public GiraTravelsTopologyConfigurationBuilder withUntilPhase(Phases untilPhase) {
            this.untilPhase = untilPhase;
            return this;
        }

        public GiraTravelsTopologyConfiguration build() {
            return new GiraTravelsTopologyConfiguration(parallelism, untilPhase);
        }
    }

    public static final class GiraTravelsTopologyConfigurationFields {
        private static final String TOPOLOGY_PREFIX = "topology.";
        public static final String TOPOLOGY_PARALLELISM = TOPOLOGY_PREFIX.concat("parallelism");
        public static final String TOPOLOGY_UNTIL_PHASE = TOPOLOGY_PREFIX.concat("until_phase");
    }
}

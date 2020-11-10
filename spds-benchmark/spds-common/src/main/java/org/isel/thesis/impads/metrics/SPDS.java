package org.isel.thesis.impads.metrics;

public enum SPDS {

    FLINK("flink"),
    STORM("storm"),
    KAFKA_STREAM("kafka_stream");

    private final String name;

    SPDS(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

package org.isel.thesis.impads.flink.topology.phases;

public enum Phases {
    INGESTION,
    INITIAL_TRANSFORMATION,
    FIRST_JOIN,
    SECOND_JOIN,
    STATIC_JOIN,
    RESULT,
    OUTPUT
}

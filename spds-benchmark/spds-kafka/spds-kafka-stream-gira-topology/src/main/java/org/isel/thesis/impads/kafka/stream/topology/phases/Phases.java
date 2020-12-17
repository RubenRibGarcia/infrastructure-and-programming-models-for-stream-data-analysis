package org.isel.thesis.impads.kafka.stream.topology.phases;

public enum Phases {
    INGESTION,
    INITIAL_TRANSFORMATION,
    FIRST_JOIN,
    SECOND_JOIN,
    STATIC_JOIN,
    RESULT,
    OUTPUT
}

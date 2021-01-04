package org.isel.thesis.impads.kafka.stream.topology.phases;

public enum Phases {
    INGESTION,
    PARSE,
    FIRST_JOIN,
    SECOND_JOIN,
    STATIC_JOIN,
    RESULT,
    OUTPUT
}

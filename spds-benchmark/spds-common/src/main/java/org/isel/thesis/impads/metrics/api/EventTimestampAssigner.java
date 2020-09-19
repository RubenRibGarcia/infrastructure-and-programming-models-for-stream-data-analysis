package org.isel.thesis.impads.metrics.api;

@FunctionalInterface
public interface EventTimestampAssigner<T> {

    long extractEventTimestamp(T obj);
}

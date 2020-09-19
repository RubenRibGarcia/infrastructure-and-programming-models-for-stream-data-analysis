package org.isel.thesis.impads.metrics.api;

import java.io.Serializable;

@FunctionalInterface
public interface SerializableEventTimestampAssigner<T> extends EventTimestampAssigner<T>, Serializable {

}

package org.isel.thesis.impads.storm.topology.bolts.operations;

import java.io.Serializable;

public interface Function<T, R> extends java.util.function.Function<T, R>, Serializable {
}

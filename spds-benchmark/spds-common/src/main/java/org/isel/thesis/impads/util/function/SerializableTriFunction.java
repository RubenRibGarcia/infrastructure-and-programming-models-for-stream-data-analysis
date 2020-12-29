package org.isel.thesis.impads.util.function;

import java.io.Serializable;

@FunctionalInterface
public interface SerializableTriFunction<T, V, U ,R> extends TriFunction<T, V, U, R>, Serializable {
}

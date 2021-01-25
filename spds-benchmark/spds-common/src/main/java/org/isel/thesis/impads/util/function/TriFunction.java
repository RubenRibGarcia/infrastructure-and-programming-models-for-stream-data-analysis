package org.isel.thesis.impads.util.function;

@FunctionalInterface
public interface TriFunction<T, V, U, R> {

    R apply(T t, V v, U u);
}

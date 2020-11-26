package org.isel.thesis.impads.storm.redis.common;

import org.apache.storm.tuple.Tuple;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface TupleMapper<T> extends Function<Tuple, T>, Serializable {

}

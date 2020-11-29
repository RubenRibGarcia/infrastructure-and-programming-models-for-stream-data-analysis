package org.isel.thesis.impads.storm.topology;

import org.apache.storm.tuple.Tuple;

import java.io.Serializable;
import java.util.function.Function;

public interface TupleMapper<T> extends Function<Tuple , T>, Serializable {
}

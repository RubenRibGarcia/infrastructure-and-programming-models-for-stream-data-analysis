package org.isel.thesis.impads.storm.low_level.topology.bolts.join;

import org.apache.storm.tuple.Tuple;
import org.isel.thesis.impads.storm.low_level.topology.bolts.operations.Function;

import java.io.Serializable;

public class TupleFieldSelector<T> implements Serializable {

    public static final long serialVersionUID = 1L;

    private Function<Tuple, T> selector;

    private TupleFieldSelector(Function<Tuple, T> selector) {
        this.selector = selector;
    }

    public static <T> TupleFieldSelector<T> selector(Function<Tuple, T> selector) {
        return new TupleFieldSelector<>(selector);
    }

    public Function<Tuple, T> getSelector() {
        return selector;
    }
}

package org.isel.thesis.impads.storm.topology.bolts.join;

import org.isel.thesis.impads.storm.topology.bolts.operations.Function;

import java.io.Serializable;

public class KeySelector<T, R> implements Serializable {

    public static final long serialVersionUID = 1L;

    private Function<T, R> selector;

    private KeySelector(Function<T, R> selector) {
        this.selector = selector;
    }

    public static <T, R> KeySelector<T, R> selector(Function<T, R> selector) {
        return new KeySelector<>(selector);
    }

    public Function<T, R> getSelector() {
        return selector;
    }
}

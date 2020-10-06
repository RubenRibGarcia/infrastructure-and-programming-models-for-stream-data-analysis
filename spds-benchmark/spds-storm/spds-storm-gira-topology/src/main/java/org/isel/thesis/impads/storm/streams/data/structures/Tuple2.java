package org.isel.thesis.impads.storm.streams.data.structures;

import java.io.Serializable;

public final class Tuple2<V1, V2> implements Serializable {

    private static final long serialVersionUID = 1L;

    public final V1 first;
    public final V2 second;

    public Tuple2(V1 first, V2 second) {
        this.first = first;
        this.second = second;
    }

    public static <V1, V2> Tuple2<V1, V2> of(V1 first, V2 second) {
        return new Tuple2<>(first, second);
    }


    public V1 getFirst() {
        return first;
    }

    public V2 getSecond() {
        return second;
    }
}

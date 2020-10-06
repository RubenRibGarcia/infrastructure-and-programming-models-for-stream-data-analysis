package org.isel.thesis.impads.storm.streams.data.structures;

import java.io.Serializable;

public final class Tuple3<V1, V2, V3> implements Serializable {

    private static final long serialVersionUID = 1L;

    public final V1 first;
    public final V2 second;
    public final V3 third;

    public Tuple3(V1 first, V2 second, V3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <V1, V2, V3> Tuple3<V1, V2, V3> of(V1 first, V2 second, V3 third) {
        return new Tuple3<>(first, second, third);
    }

    public V1 getFirst() {
        return first;
    }

    public V2 getSecond() {
        return second;
    }

    public V3 getThird() {
        return third;
    }
}

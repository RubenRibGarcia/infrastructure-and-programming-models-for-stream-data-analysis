package org.isel.thesis.impads.structures;

import java.io.Serializable;

public final class Tuple4<V1, V2, V3, V4> implements Serializable {

    private static final long serialVersionUID = 1L;

    public final V1 first;
    public final V2 second;
    public final V3 third;
    public final V4 fourth;

    public Tuple4(V1 first, V2 second, V3 third, V4 fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public static <V1, V2, V3, V4> Tuple4<V1, V2, V3, V4> of(V1 first, V2 second, V3 third, V4 fourth) {
        return new Tuple4<>(first, second, third, fourth);
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

    public V4 getFourth() {
        return fourth;
    }
}

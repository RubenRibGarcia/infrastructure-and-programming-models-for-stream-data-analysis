package org.isel.thesis.impads.kafka.stream.data.structures;

public class Tuple3<V1, V2, V3> {

    private final V1 first;
    private final V2 second;
    private final V3 third;

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

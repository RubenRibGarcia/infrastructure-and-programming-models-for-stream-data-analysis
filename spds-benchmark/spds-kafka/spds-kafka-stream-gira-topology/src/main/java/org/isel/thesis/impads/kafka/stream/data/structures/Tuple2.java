package org.isel.thesis.impads.kafka.stream.data.structures;

public class Tuple2<V1, V2> {

    private final V1 first;
    private final V2 second;

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

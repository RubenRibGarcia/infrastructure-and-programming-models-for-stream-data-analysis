package org.isel.thesis.impads.storm.low_level.topology.bolts.join;

import java.io.Serializable;

public interface JoinFunction<T1, T2, R> extends Serializable {

    R apply(T1 from, T2 join);
}

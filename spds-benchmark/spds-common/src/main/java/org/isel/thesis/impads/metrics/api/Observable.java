package org.isel.thesis.impads.metrics.api;

import java.io.Serializable;

public interface Observable<T> extends Serializable {

    long getEventTimestamp();

    long getIngestionTimestamp();

    long getProcessedTimestamp();

    T getData();
}

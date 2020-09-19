package org.isel.thesis.impads.metrics;

import org.isel.thesis.impads.metrics.api.Observable;

import java.io.Serializable;

public class ObservableImpl<T> implements Observable<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private final long eventTimestamp;
    private final long ingestionTimestamp;
    private final T data;

    private final long processedTimestamp;

    private ObservableImpl(T data, long eventTimestamp, long ingestionTimestamp, long processedTimestamp) {
        this.data = data;
        this.eventTimestamp = eventTimestamp;
        this.ingestionTimestamp = ingestionTimestamp;
        this.processedTimestamp = processedTimestamp;
    }

    private ObservableImpl(T data, long eventTimestamp, long ingestionTimestamp) {
        this(data, eventTimestamp, ingestionTimestamp, 0L);
    }

    public static <T> Observable<T> of(T data, long eventTimestamp, long ingestionTimestamp) {
        return new ObservableImpl<>(data, eventTimestamp, ingestionTimestamp);
    }

    public static <T> Observable<T> of(T data, long eventTimestamp, long ingestionTimestamp, long processedTimestamp) {
        return new ObservableImpl<>(data, eventTimestamp, ingestionTimestamp, processedTimestamp);
    }

    public static <T> Observable<T> join(T data, Observable<?> right, Observable<?> left) {
        long joinedEventTimestamp = Math.max(right.getEventTimestamp(), left.getEventTimestamp());
        long joinedIngestionTimestamp = Math.max(right.getIngestionTimestamp(), left.getIngestionTimestamp());

        return new ObservableImpl<>(data
                , joinedEventTimestamp
                , joinedIngestionTimestamp);
    }

    public static <T> Observable<T> join(T data, Observable<?> right, Observable<?> left, long processedTimestamp) {
        long joinedEventTimestamp = Math.max(right.getEventTimestamp(), left.getEventTimestamp());
        long joinedIngestionTimestamp = Math.max(right.getIngestionTimestamp(), left.getIngestionTimestamp());

        return new ObservableImpl<>(data
                , joinedEventTimestamp
                , joinedIngestionTimestamp
                , processedTimestamp);
    }

    public static <R,T> Observable<R> map(Observable<T> from, R to) {
        return new ObservableImpl<>(to, from.getEventTimestamp(), from.getIngestionTimestamp(), from.getProcessedTimestamp());
    }

    @Override
    public long getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public long getIngestionTimestamp() {
        return ingestionTimestamp;
    }

    @Override
    public long getProcessedTimestamp() {
        return processedTimestamp;
    }

    @Override
    public T getData() {
        return data;
    }

}

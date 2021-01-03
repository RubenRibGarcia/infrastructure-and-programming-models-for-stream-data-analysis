package org.isel.thesis.impads.metrics;

import java.io.Serializable;

public class Observable<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private long eventTimestamp;
    private long ingestionTimestamp;
    private T data;

    private long processedTimestamp;

    public Observable() { }

    public Observable(T data, long eventTimestamp, long ingestionTimestamp, long processedTimestamp) {
        this.data = data;
        this.eventTimestamp = eventTimestamp;
        this.ingestionTimestamp = ingestionTimestamp;
        this.processedTimestamp = processedTimestamp;
    }

    public Observable(T data, long eventTimestamp, long ingestionTimestamp) {
        this(data, eventTimestamp, ingestionTimestamp, 0L);
    }

    public static <T> Observable<T> of(T data, long eventTimestamp, long ingestionTimestamp) {
        return new Observable<>(data, eventTimestamp, ingestionTimestamp);
    }

    public static <T> Observable<T> of(T data, long eventTimestamp, long ingestionTimestamp, long processedTimestamp) {
        return new Observable<>(data, eventTimestamp, ingestionTimestamp, processedTimestamp);
    }

    public <R> Observable<R> join(R data, Observable<?> other) {
        long joinedEventTimestamp = Math.max(this.getEventTimestamp(), other.getEventTimestamp());
        long joinedIngestionTimestamp = Math.max(this.getIngestionTimestamp(), other.getIngestionTimestamp());

        return new Observable<>(data
                , joinedEventTimestamp
                , joinedIngestionTimestamp);
    }

    public <R> Observable<R> join(R data, Observable<?> other, long processedTimestamp) {
        long joinedEventTimestamp = Math.max(this.getEventTimestamp(), other.getEventTimestamp());
        long joinedIngestionTimestamp = Math.max(this.getIngestionTimestamp(), other.getIngestionTimestamp());

        return new Observable<>(data
                , joinedEventTimestamp
                , joinedIngestionTimestamp
                , processedTimestamp);
    }

    public <R> Observable<R> map(R to) {
        return new Observable<>(to, this.getEventTimestamp(), this.getIngestionTimestamp(), this.getProcessedTimestamp());
    }

    public long getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public long getIngestionTimestamp() {
        return ingestionTimestamp;
    }

    public void setIngestionTimestamp(long ingestionTimestamp) {
        this.ingestionTimestamp = ingestionTimestamp;
    }

    public long getProcessedTimestamp() {
        return processedTimestamp;
    }

    public void setProcessedTimestamp(long processedTimestamp) {
        this.processedTimestamp = processedTimestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

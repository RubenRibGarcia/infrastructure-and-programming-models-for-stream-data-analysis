package org.isel.thesis.impads.flink.topology.models;

import java.io.Serializable;

public class SimplifiedGiraTravelsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String identifier;
    private String geometry;
    private long eventTimestamp;

    public SimplifiedGiraTravelsModel() { }

    public SimplifiedGiraTravelsModel(String identifier, String geometry, long eventTimestamp) {
        this.identifier = identifier;
        this.geometry = geometry;
        this.eventTimestamp = eventTimestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public long getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
}

package org.isel.thesis.impads.flink.topology.models;

import java.io.Serializable;

public class SimplifiedWazeJamsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String identifier;
    private final String geometry;
    private final long eventTimestamp;

    public SimplifiedWazeJamsModel(String identifier, String geometry, long eventTimestamp) {
        this.identifier = identifier;
        this.geometry = geometry;
        this.eventTimestamp = eventTimestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getGeometry() {
        return geometry;
    }

    public long getEventTimestamp() {
        return eventTimestamp;
    }
}

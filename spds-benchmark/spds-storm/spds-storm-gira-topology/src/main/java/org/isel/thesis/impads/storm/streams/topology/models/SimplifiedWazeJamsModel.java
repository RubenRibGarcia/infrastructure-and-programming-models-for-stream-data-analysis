package org.isel.thesis.impads.storm.streams.topology.models;

import java.io.Serializable;

public class SimplifiedWazeJamsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String identifier;
    private final String geometry;
    private final long evenTimestamp;

    public SimplifiedWazeJamsModel(String identifier, String geometry, long evenTimestamp) {
        this.identifier = identifier;
        this.geometry = geometry;
        this.evenTimestamp = evenTimestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getGeometry() {
        return geometry;
    }

    public long getEvenTimestamp() {
        return evenTimestamp;
    }
}

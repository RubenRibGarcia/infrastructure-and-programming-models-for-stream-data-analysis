package org.isel.thesis.impads.kafka.stream.topology.model;

import java.io.Serializable;

public class SimplifiedWazeJamsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String identifier;
    private final String geometry;

    public SimplifiedWazeJamsModel(String identifier, String geometry) {
        this.identifier = identifier;
        this.geometry = geometry;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getGeometry() {
        return geometry;
    }
}

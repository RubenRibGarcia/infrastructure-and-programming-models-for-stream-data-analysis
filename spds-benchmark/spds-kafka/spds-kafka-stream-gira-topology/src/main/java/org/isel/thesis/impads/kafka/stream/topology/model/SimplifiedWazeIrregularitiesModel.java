package org.isel.thesis.impads.kafka.stream.topology.model;

import java.io.Serializable;

public class SimplifiedWazeIrregularitiesModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String identifier;
    private final String geometry;
    private final long eventTimestamp;

    public SimplifiedWazeIrregularitiesModel(String identifier, String geometry, long eventTimestamp) {
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

package org.isel.thesis.impads.storm.datamodel;

import java.io.Serializable;

public class SimplifiedGiraTravelsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String identifier;
    private final String geometry;

    public SimplifiedGiraTravelsModel(String identifier, String geometry) {
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

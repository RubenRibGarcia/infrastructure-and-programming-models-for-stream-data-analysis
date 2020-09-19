package org.isel.thesis.impads.giragen.datamodel.api.waze.adapter;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public interface WazeJamsDataAdapter {

    enum JamIntensityType {
        MEDIUM("Medium"),
        SMALL("Small"),
        LARGE("Large"),
        HUGE("Huge"),
        NONE("NONE");

        private final String type;

        JamIntensityType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static JamIntensityType fromValue(String value) {
            List<JamIntensityType> values = new LinkedList<>(Arrays.asList(JamIntensityType.values()));
            for (JamIntensityType v : values) {
                if (v.getType().equals(value)) {
                    return v;
                }
            }

            throw new IllegalArgumentException("No enum value present with " + value);
        }
    }

    Long getId();

    String getCity();

    Integer getLevel();

    Integer getLength();

    JamIntensityType getJamIntensityType();

    Long getUuid();

    String getEndNode();

    Float getSpeed();

    Integer getRoadType();

    Integer getDelay();

    String getStreet();

    Long getPubMillis();

    Instant getCreationDate();

    Instant getLastModDate();

    String getGeometry();
}

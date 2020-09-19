package org.isel.thesis.impads.giragen.datamodel.api.waze.adapter;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public interface WazeIrregularitiesDataAdapter {

    enum IrregularityIntensityType {
        MEDIUM("Medium"),
        SMALL("Small"),
        LARGE("Large"),
        HUGE("Huge");

        private final String type;

        IrregularityIntensityType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static IrregularityIntensityType fromValue(String value) {
            List<IrregularityIntensityType> values = new LinkedList<>(Arrays.asList(IrregularityIntensityType.values()));
            for (IrregularityIntensityType v : values) {
                if (v.getType().equals(value)) {
                    return v;
                }
            }

            throw new IllegalArgumentException("No enum value present with " + value);
        }
    }

    Long getId();

    Integer getnThumbsUp();

    Instant getUpdateDate();

    Integer getTrend();

    String getCity();

    Long getDetectionDateMillis();

    IrregularityIntensityType getIrregularityIntensityType();

    String getEndNode();

    Float getSpeed();

    Integer getSeconds();

    String getStartNode();

    String getStreet();

    Integer getJamLevel();

    Long getWazeId();

    Boolean isHighway();

    Integer getDelaySeconds();

    Integer getSeverity();

    Integer getAlertsCount();

    Integer getLength();

    Long getUpdateDateMillis();

    Instant getDetectionDate();

    Float getRegularSpeed();

    Instant getCreationDate();

    Instant getLastModDate();

    String getGeometry();

}

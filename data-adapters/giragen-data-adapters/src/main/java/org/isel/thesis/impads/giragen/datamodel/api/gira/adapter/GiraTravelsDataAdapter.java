package org.isel.thesis.impads.giragen.datamodel.api.gira.adapter;


import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public interface GiraTravelsDataAdapter {

    enum BicycleType {
        CASUAL("C"),
        ELECTRIC("E"),
        UNDEFINED("");

        private String type;

        BicycleType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        @Override
        public String toString() {
            return this.type;
        }

        public static BicycleType fromValue(String value) {
            List<BicycleType> values = new LinkedList<>(Arrays.asList(BicycleType.values()));
            for (BicycleType v : values) {
                if (v.getType().equals(value)) {
                    return v;
                }
            }

            throw new IllegalArgumentException("No enum value present with " + value);
        }
    }

    Long getId();

    Instant getDateStart();

    Instant getDateEnd();

    Float getDistance();

    Integer getStationStart();

    Integer getStationEnd();

    String getBikeRfid();

    BicycleType getBicycleType();

    String getGeometry();

    Integer getNumberOfVertices();
}

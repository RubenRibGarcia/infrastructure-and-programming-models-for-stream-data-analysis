package org.isel.thesis.impads.storm.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.storm.tuple.Fields;

import java.io.Serializable;

public class GiraTravelsWithWazeResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long giraTravelIdentifier;
    private final Long wazeJamIdentifier;
    private final Long wazeIrregularityIdentifier;
    private final String giraTravelGeom;
    private final String wazeJamGeom;
    private final String wazeIrregularityGeom;
    private final boolean jamAndIrregularityMatches;
    private final long createTimestamp;

    public GiraTravelsWithWazeResult(Long giraTravelIdentifier
            , Long wazeJamIdentifier
            , Long wazeIrregularityIdentifier
            , String giraTravelGeom
            , String wazeJamGeom
            , String wazeIrregularityGeom
            , boolean jamAndIrregularityMatches
            , long createTimestamp) {
        this.giraTravelIdentifier = giraTravelIdentifier;
        this.wazeJamIdentifier = wazeJamIdentifier;
        this.wazeIrregularityIdentifier = wazeIrregularityIdentifier;
        this.giraTravelGeom = giraTravelGeom;
        this.wazeIrregularityGeom = wazeIrregularityGeom;
        this.wazeJamGeom = wazeJamGeom;
        this.jamAndIrregularityMatches = jamAndIrregularityMatches;
        this.createTimestamp = createTimestamp;
    }

    @JsonProperty(value = "gira_travel_identifier")
    public Long getGiraTravelIdentifier() {
        return giraTravelIdentifier;
    }

    @JsonProperty(value = "waze_jam_identifier")
    public Long getWazeJamIdentifier() {
        return wazeJamIdentifier;
    }

    @JsonProperty(value = "waze_irregularity_identifier")
    public Long getWazeIrregularityIdentifier() {
        return wazeIrregularityIdentifier;
    }

    @JsonProperty(value = "gira_travel_geom")
    public String getGiraTravelGeom() {
        return giraTravelGeom;
    }

    @JsonProperty(value = "waze_jam_geom")
    public String getWazeJamGeom() {
        return wazeJamGeom;
    }

    @JsonProperty(value = "waze_irregularity_geom")
    public String getWazeIrregularityGeom() {
        return wazeIrregularityGeom;
    }

    @JsonProperty(value = "jam_and_irregularity_matches")
    public boolean jamAndIrregularityMatches() {
        return jamAndIrregularityMatches;
    }

    @JsonProperty(value = "create_timestamp")
    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public static Fields getTupleField() {
        return new Fields("gira_travel_identifier", "gira_travel_geom", "waze_jam_identifier", "waze_jam_geom", "waze_irregularity_identifier", "waze_irregularity_geom", "jam_and_irregularity_matches", "create_timestamp");
    }

    public static final class Builder {
        private Long giraTravelIdentifier;
        private Long wazeJamIdentifier;
        private Long wazeIrregularityIdentifier;
        private String giraTravelGeom;
        private String wazeJamGeom;
        private String wazeIrregularityGeom;
        private boolean jamAndIrregularityMatches;
        private long createTimestamp;

        public static Builder builder() {
            return new Builder();
        }

        public Builder withGiraTravelIdentifier(Long giraTravelIdentifier) {
            this.giraTravelIdentifier = giraTravelIdentifier;
            return this;
        }

        public Builder withWazeJamIdentifier(Long wazeJamIdentifier) {
            this.wazeJamIdentifier = wazeJamIdentifier;
            return this;
        }

        public Builder withWazeIrregularityIdentifier(Long wazeIrregularityIdentifier) {
            this.wazeIrregularityIdentifier = wazeIrregularityIdentifier;
            return this;
        }

        public Builder withGiraTravelGeom(String giraTravelGeom) {
            this.giraTravelGeom = giraTravelGeom;
            return this;
        }

        public Builder withWazeJamGeom(String wazeJamGeom) {
            this.wazeJamGeom = wazeJamGeom;
            return this;
        }

        public Builder withWazeIrregularityGeom(String wazeIrregularityGeom) {
            this.wazeIrregularityGeom = wazeIrregularityGeom;
            return this;
        }

        public Builder jamAndIrregularityMatches(boolean jamAndIrregularityMatches) {
            this.jamAndIrregularityMatches = jamAndIrregularityMatches;
            return this;
        }

        public Builder withCreateTimestamp(long createTimestamp) {
            this.createTimestamp = createTimestamp;
            return this;
        }

        public GiraTravelsWithWazeResult build() {
            return new GiraTravelsWithWazeResult(giraTravelIdentifier
                    , wazeJamIdentifier
                    , wazeIrregularityIdentifier
                    , giraTravelGeom
                    , wazeJamGeom
                    , wazeIrregularityGeom
                    , jamAndIrregularityMatches
                    , createTimestamp);
        }
    }
}

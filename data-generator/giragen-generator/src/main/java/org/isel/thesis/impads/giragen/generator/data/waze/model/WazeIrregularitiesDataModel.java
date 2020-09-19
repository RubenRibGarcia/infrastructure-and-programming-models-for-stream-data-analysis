package org.isel.thesis.impads.giragen.generator.data.waze.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.csv.CSVRecord;
import org.isel.thesis.impads.giragen.data.api.EventTimestampGenerator;
import org.isel.thesis.impads.giragen.datamodel.api.waze.adapter.WazeIrregularitiesDataAdapter;
import org.isel.thesis.impads.giragen.generator.data.JsonDataModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class WazeIrregularitiesDataModel implements WazeIrregularitiesDataAdapter, JsonDataModel {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnn");

    private final Long id;
    private final Integer nThumbsUp;
    private final Instant updateDate;
    private final Integer trend;
    private final String city;
    private final Long detectionDateMillis;
    private final IrregularityIntensityType irregularityIntensityType;
    private final String endNode;
    private final Float speed;
    private final Integer seconds;
    private final String startNode;
    private final String street;
    private final Integer jamLevel;
    private final Long wazeId;
    private final Boolean highway;
    private final Integer delaySeconds;
    private final Integer severity;
    private final Integer alertsCount;
    private final Integer length;
    private final Long updateDateMillis;
    private final Instant detectionDate;
    private final Float regularSpeed;
    private final Instant creationDate;
    private final Instant lastModDate;
    private final String geometry;


    public WazeIrregularitiesDataModel(Long id
            , Integer nThumbsUp
            , Instant updateDate
            , Integer trend
            , String city
            , Long detectionDateMillis
            , IrregularityIntensityType irregularityIntensityType
            , String endNode
            , Float speed
            , Integer seconds
            , String startNode
            , String street
            , Integer jamLevel
            , Long wazeId
            , Boolean highway
            , Integer delaySeconds
            , Integer severity
            , Integer alertsCount
            , Integer length
            , Long updateDateMillis
            , Instant detectionDate
            , Float regularSpeed
            , Instant creationDate
            , Instant lastModDate
            , String geometry) {

        this.id = id;
        this.nThumbsUp = nThumbsUp;
        this.updateDate = updateDate;
        this.trend = trend;
        this.city = city;
        this.detectionDateMillis = detectionDateMillis;
        this.irregularityIntensityType = irregularityIntensityType;
        this.endNode = endNode;
        this.speed = speed;
        this.seconds = seconds;
        this.startNode = startNode;
        this.street = street;
        this.jamLevel = jamLevel;
        this.wazeId = wazeId;
        this.highway = highway;
        this.delaySeconds = delaySeconds;
        this.severity = severity;
        this.alertsCount = alertsCount;
        this.length = length;
        this.updateDateMillis = updateDateMillis;
        this.detectionDate = detectionDate;
        this.regularSpeed = regularSpeed;
        this.creationDate = creationDate;
        this.lastModDate = lastModDate;
        this.geometry = geometry;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("n_thumbs_up")
    public Integer getnThumbsUp() {
        return nThumbsUp;
    }

    @JsonProperty("update_date")
    public Instant getUpdateDate() {
        return updateDate;
    }

    @JsonProperty("trend")
    public Integer getTrend() {
        return trend;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("detection_date_millis")
    public Long getDetectionDateMillis() {
        return detectionDateMillis;
    }

    @JsonProperty("type")
    public IrregularityIntensityType getIrregularityIntensityType() {
        return irregularityIntensityType;
    }

    @JsonProperty("end_node")
    public String getEndNode() {
        return endNode;
    }

    @JsonProperty("speed")
    public Float getSpeed() {
        return speed;
    }

    @JsonProperty("seconds")
    public Integer getSeconds() {
        return seconds;
    }

    @JsonProperty("start_node")
    public String getStartNode() {
        return startNode;
    }

    @JsonProperty("street")
    public String getStreet() {
        return street;
    }

    @JsonProperty("jam_level")
    public Integer getJamLevel() {
        return jamLevel;
    }

    @JsonProperty("waze_id")
    public Long getWazeId() {
        return wazeId;
    }

    @JsonProperty("is_highway")
    public Boolean isHighway() {
        return highway;
    }

    @JsonProperty("delay_seconds")
    public Integer getDelaySeconds() {
        return delaySeconds;
    }

    @JsonProperty("severity")
    public Integer getSeverity() {
        return severity;
    }

    @JsonProperty("alerts_count")
    public Integer getAlertsCount() {
        return alertsCount;
    }

    @JsonProperty("length")
    public Integer getLength() {
        return length;
    }

    @JsonProperty("update_date_millis")
    public Long getUpdateDateMillis() {
        return updateDateMillis;
    }

    @JsonProperty("detection_date")
    public Instant getDetectionDate() {
        return detectionDate;
    }

    @JsonProperty("regular_speed")
    public Float getRegularSpeed() {
        return regularSpeed;
    }

    @JsonProperty("creation_date")
    public Instant getCreationDate() {
        return creationDate;
    }

    @JsonProperty("last_mod_date")
    public Instant getLastModDate() {
        return lastModDate;
    }

    @JsonProperty("geom")
    public String getGeometry() {
        return geometry;
    }

    public static WazeIrregularitiesDataModel fromCSV(final CSVRecord record
            , final EventTimestampGenerator synthesizer) {
        long eventTimestamp = synthesizer.getNextInstant().toEpochMilli();
        return Builder.builder()
                .withId(Long.parseLong(record.get("id")))
                .withNThumbsUp(Integer.parseInt(record.get("n_thumbs_up")))
                .withUpdateDate(LocalDateTime.parse(record.get("update_date"), formatter).toInstant(ZoneOffset.UTC))
                .withTrend(Integer.parseInt(record.get("trend")))
                .withCity(record.get("city"))
                .withDetectionDateMillis(eventTimestamp)
                .withIrregularityIntensityType(IrregularityIntensityType.fromValue(record.get("type")))
                .withEndNode(record.get("end_node"))
                .withSpeed(Float.parseFloat(record.get("speed")))
                .withSeconds(Integer.parseInt(record.get("seconds")))
                .withStartNode(record.get("start_node"))
                .withStreet(record.get("street"))
                .withJamLevel(Integer.parseInt(record.get("jam_level")))
                .withWazeId(Long.parseLong(record.get("waze_id")))
                .withHighway(Boolean.parseBoolean(record.get("highway")))
                .withDelaySeconds(Integer.parseInt(record.get("delay_seconds")))
                .withSeverity(Integer.parseInt(record.get("severity")))
                .withAlertsCount(Integer.parseInt(record.get("alerts_count")))
                .withLength(Integer.parseInt(record.get("length")))
                .withUpdateDateMillis(new BigDecimal(record.get("update_date_millis")).toBigInteger().longValue())
                .withDetectionDate(Instant.ofEpochMilli(eventTimestamp))
                .withRegularSpeed(Float.parseFloat(record.get("regular_speed")))
                .withCreationDate(LocalDateTime.parse(record.get("creation_date"), formatter).toInstant(ZoneOffset.UTC))
                .withLastModDate(LocalDateTime.parse(record.get("lastmod_date"), formatter).toInstant(ZoneOffset.UTC))
                .withGeometry(record.get("geom"))
                .build();
    }

    public static final class Builder {
        private Long id;
        private Integer nThumbsUp;
        private Instant updateDate;
        private Integer trend;
        private String city;
        private Long detectionDateMillis;
        private IrregularityIntensityType irregularityIntensityType;
        private String endNode;
        private Float speed;
        private Integer seconds;
        private String startNode;
        private String street;
        private Integer jamLevel;
        private Long wazeId;
        private Boolean highway;
        private Integer delaySeconds;
        private Integer severity;
        private Integer alertsCount;
        private Integer length;
        private Long updateDateMillis;
        private Instant detectionDate;
        private Float regularSpeed;
        private Instant creationDate;
        private Instant lastModDate;
        private String geometry;

        private Builder() { }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withNThumbsUp(Integer nThumbsUp) {
            this.nThumbsUp = nThumbsUp;
            return this;
        }

        public Builder withUpdateDate(Instant updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Builder withTrend(Integer trend) {
            this.trend = trend;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withDetectionDateMillis(Long detectionDateMillis) {
            this.detectionDateMillis = detectionDateMillis;
            return this;
        }

        public Builder withIrregularityIntensityType(IrregularityIntensityType irregularityIntensityType) {
            this.irregularityIntensityType = irregularityIntensityType;
            return this;
        }

        public Builder withEndNode(String endNode) {
            this.endNode = endNode;
            return this;
        }

        public Builder withSpeed(Float speed) {
            this.speed = speed;
            return this;
        }

        public Builder withSeconds(Integer seconds) {
            this.seconds = seconds;
            return this;
        }

        public Builder withStartNode(String startNode) {
            this.startNode = startNode;
            return this;
        }

        public Builder withStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder withJamLevel(Integer jamLevel) {
            this.jamLevel = jamLevel;
            return this;
        }

        public Builder withWazeId(Long wazeId) {
            this.wazeId = wazeId;
            return this;
        }

        public Builder withHighway(Boolean highway) {
            this.highway = highway;
            return this;
        }

        public Builder withDelaySeconds(Integer delaySeconds) {
            this.delaySeconds = delaySeconds;
            return this;
        }

        public Builder withSeverity(Integer severity) {
            this.severity = severity;
            return this;
        }

        public Builder withAlertsCount(Integer alertsCount) {
            this.alertsCount = alertsCount;
            return this;
        }

        public Builder withLength(Integer length) {
            this.length = length;
            return this;
        }

        public Builder withUpdateDateMillis(Long updateDateMillis) {
            this.updateDateMillis = updateDateMillis;
            return this;
        }

        public Builder withDetectionDate(Instant detectionDate) {
            this.detectionDate = detectionDate;
            return this;
        }

        public Builder withRegularSpeed(Float regularSpeed) {
            this.regularSpeed = regularSpeed;
            return this;
        }

        public Builder withCreationDate(Instant creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder withLastModDate(Instant lastModDate) {
            this.lastModDate = lastModDate;
            return this;
        }

        public Builder withGeometry(String geometry) {
            this.geometry = geometry;
            return this;
        }

        public WazeIrregularitiesDataModel build() {
            return new WazeIrregularitiesDataModel(id
                    , nThumbsUp
                    , updateDate
                    , trend
                    , city
                    , detectionDateMillis
                    , irregularityIntensityType
                    , endNode
                    , speed
                    , seconds
                    , startNode
                    , street
                    , jamLevel
                    , wazeId
                    , highway
                    , delaySeconds
                    , severity
                    , alertsCount
                    , length
                    , updateDateMillis
                    , detectionDate
                    , regularSpeed
                    , creationDate
                    , lastModDate
                    , geometry);
        }
    }
}

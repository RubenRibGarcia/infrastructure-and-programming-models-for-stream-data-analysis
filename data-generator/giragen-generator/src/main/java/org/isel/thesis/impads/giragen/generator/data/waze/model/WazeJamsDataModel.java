package org.isel.thesis.impads.giragen.generator.data.waze.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vavr.control.Either;
import org.apache.commons.csv.CSVRecord;
import org.isel.thesis.impads.giragen.data.api.EventTimestampGenerator;
import org.isel.thesis.impads.giragen.datamodel.api.waze.adapter.WazeJamsDataAdapter;
import org.isel.thesis.impads.giragen.generator.api.GeneratorError;
import org.isel.thesis.impads.giragen.generator.data.JsonDataModel;
import org.isel.thesis.impads.giragen.generator.error.GeneratorIOError;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class WazeJamsDataModel implements WazeJamsDataAdapter, JsonDataModel {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnn");

    private final Long id;
    private final String city;
    private final Integer level;
    private final Integer length;
    private final JamIntensityType jamIntensityType;
    private final Long uuid;
    private final String endNode;
    private final Float speed;
    private final Integer roadType;
    private final Integer delay;
    private final String street;
    private final Long pubMillis;
    private final Instant creationDate;
    private final Instant lastModDate;
    private final String geometry;

    public WazeJamsDataModel(Long id
            , String city
            , Integer level
            , Integer length
            , JamIntensityType jamIntensityType
            , Long uuid
            , String endNode
            , Float speed
            , Integer roadType
            , Integer delay
            , String street
            , Long pubMillis
            , Instant creationDate
            , Instant lastModDate
            , String geometry) {
        this.id = id;
        this.city = city;
        this.level = level;
        this.length = length;
        this.jamIntensityType = jamIntensityType;
        this.uuid = uuid;
        this.endNode = endNode;
        this.speed = speed;
        this.roadType = roadType;
        this.delay = delay;
        this.street = street;
        this.pubMillis = pubMillis;
        this.creationDate = creationDate;
        this.lastModDate = lastModDate;
        this.geometry = geometry;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("level")
    public Integer getLevel() {
        return level;
    }

    @JsonProperty("length")
    public Integer getLength() {
        return length;
    }

    @JsonProperty("type")
    public JamIntensityType getJamIntensityType() {
        return jamIntensityType;
    }

    @JsonProperty("uuid")
    public Long getUuid() {
        return uuid;
    }

    @JsonProperty("end_node")
    public String getEndNode() {
        return endNode;
    }

    @JsonProperty("speed")
    public Float getSpeed() {
        return speed;
    }

    @JsonProperty("road_type")
    public Integer getRoadType() {
        return roadType;
    }

    @JsonProperty("delay")
    public Integer getDelay() {
        return delay;
    }

    @JsonProperty("street")
    public String getStreet() {
        return street;
    }

    @JsonProperty("pub_millis")
    public Long getPubMillis() {
        return pubMillis;
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

    public static Either<GeneratorError, WazeJamsDataModel> fromCSV(final CSVRecord record
            , final EventTimestampGenerator synthesizer) {
        long currentTimestamp = synthesizer.getNextInstant().toEpochMilli();
        try {
            return Either.right(Builder.builder()
                    .withId(Long.parseLong(record.get("id")))
                    .withCity(record.get("city"))
                    .withLevel(Integer.parseInt(record.get("level")))
                    .withLength(Integer.parseInt(record.get("length")))
                    .withType(JamIntensityType.fromValue(record.get("type")))
                    .withUuid(Long.parseLong(record.get("uuid")))
                    .withEndNode(record.get("end_node"))
                    .withSpeed(Float.parseFloat(record.get("speed")))
                    .withRoadType(Integer.parseInt(record.get("road_type")))
                    .withDelay(Integer.parseInt(record.get("delay")))
                    .withStreet(record.get("street"))
                    .withPubMillis(currentTimestamp)
                    .withCreationDate(LocalDateTime.parse(record.get("creation_date"), formatter).toInstant(ZoneOffset.UTC))
                    .withLastModeDate(LocalDateTime.parse(record.get("lastmod_date"), formatter).toInstant(ZoneOffset.UTC))
                    .withGeometry(record.get("geom"))
                    .build());
        }
        catch (Throwable e) {
            return Either.left(GeneratorIOError.error("Error: " + e.getMessage() + " at record " + record.getRecordNumber()));
        }
    }

    public static final class Builder {
        private Long id;
        private String city;
        private Integer level;
        private Integer length;
        private JamIntensityType jamIntensityType;
        private Long uuid;
        private String endNode;
        private Float speed;
        private Integer roadType;
        private Integer delay;
        private String street;
        private Long pubMillis;
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

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withLevel(Integer level) {
            this.level = level;
            return this;
        }

        public Builder withLength(Integer length) {
            this.length = length;
            return this;
        }

        public Builder withType(JamIntensityType type) {
            this.jamIntensityType = type;
            return this;
        }

        public Builder withUuid(Long uuid) {
            this.uuid = uuid;
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

        public Builder withRoadType(Integer roadType) {
            this.roadType = roadType;
            return this;
        }

        public Builder withDelay(Integer delay) {
            this.delay = delay;
            return this;
        }

        public Builder withStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder withPubMillis(Long pubMillis) {
            this.pubMillis = pubMillis;
            return this;
        }

        public Builder withCreationDate(Instant creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder withLastModeDate(Instant lastModDate) {
            this.lastModDate = lastModDate;
            return this;
        }

        public Builder withGeometry(String geometry) {
            this.geometry = geometry;
            return this;
        }

        public WazeJamsDataModel build() {
            return new WazeJamsDataModel(id
                    , city
                    , level
                    , length
                    , jamIntensityType
                    , uuid
                    , endNode
                    , speed
                    , roadType
                    , delay
                    , street
                    , pubMillis
                    , creationDate
                    , lastModDate
                    , geometry);
        }

    }

}

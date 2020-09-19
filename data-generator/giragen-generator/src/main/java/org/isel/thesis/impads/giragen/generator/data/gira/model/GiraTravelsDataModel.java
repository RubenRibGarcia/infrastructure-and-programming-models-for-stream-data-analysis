package org.isel.thesis.impads.giragen.generator.data.gira.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vavr.control.Either;
import org.apache.commons.csv.CSVRecord;
import org.isel.thesis.impads.giragen.data.api.EventTimestampGenerator;
import org.isel.thesis.impads.giragen.datamodel.api.gira.adapter.GiraTravelsDataAdapter;
import org.isel.thesis.impads.giragen.generator.api.GeneratorError;
import org.isel.thesis.impads.giragen.generator.data.JsonDataModel;
import org.isel.thesis.impads.giragen.generator.error.GeneratorIOError;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class GiraTravelsDataModel implements GiraTravelsDataAdapter
        , JsonDataModel {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnn");

    private final Long id;
    private final Instant dateStart;
    private final Instant dateEnd;
    private final Float distance;
    private final Integer stationStart;
    private final Integer stationEnd;
    private final String bikeRfid;
    private final BicycleType bicycleType;
    private final String geometry;
    private final Integer numberOfVertices;

    public GiraTravelsDataModel(long id
            , Instant dateStart
            , Instant dateEnd
            , float distance
            , int stationStart
            , int stationEnd
            , String bikeRfid
            , BicycleType bicycleType
            , String geometry
            , Integer numberOfVertices) {
        this.id = id;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.distance = distance;
        this.stationStart = stationStart;
        this.stationEnd = stationEnd;
        this.bikeRfid = bikeRfid;
        this.bicycleType = bicycleType;
        this.geometry = geometry;
        this.numberOfVertices = numberOfVertices;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("date_start")
    public Instant getDateStart() {
        return dateStart;
    }

    @JsonProperty("date_end")
    public Instant getDateEnd() {
        return dateEnd;
    }

    @JsonProperty("distance")
    public Float getDistance() {
        return distance;
    }

    @JsonProperty("station_start")
    public Integer getStationStart() {
        return stationStart;
    }

    @JsonProperty("station_end")
    public Integer getStationEnd() {
        return stationEnd;
    }

    @JsonProperty("bike_rfid")
    public String getBikeRfid() {
        return bikeRfid;
    }

    @JsonProperty("bicycle_type")
    public BicycleType getBicycleType() {
        return bicycleType;
    }

    @JsonProperty("geom")
    public String getGeometry() {
        return geometry;
    }

    @JsonProperty("num_vertices")
    public Integer getNumberOfVertices() {
        return numberOfVertices;
    }

    public static Either<GeneratorError, GiraTravelsDataModel> fromCSV(CSVRecord record, EventTimestampGenerator synthesizer) {
        try {
            return Either.right(GiraTravelsDataModel.Builder.builder()
                    .withId(Long.parseLong(record.get("id")))
                    .withDateStart(synthesizer.getNextInstant())
                    .withDateEnd(LocalDateTime.parse(record.get("date_end"), formatter).toInstant(ZoneOffset.UTC))
                    .withDistance(Float.parseFloat(record.get("distance").isEmpty() ? "0.0" : record.get("distance")))
                    .withStationStart(Integer.parseInt(record.get("station_start")))
                    .withStationEnd(Integer.parseInt(record.get("station_end").equals("NUL") ? "0" : record.get("station_end")))
                    .withBikeRfid(record.get("bike_rfid"))
                    .withBicycleType(BicycleType.fromValue(record.get("Tipo_Bicicleta")))
                    .withGeometry(record.get("geom"))
                    .withNumberOfVertices(record.get("num_vertices").isEmpty() ? 0 : Integer.parseInt(record.get("num_vertices")))
                    .build());
        }
        catch (Throwable e) {
            return Either.left(GeneratorIOError.error("Error: " + e.getMessage() + " at record " + record.getRecordNumber()));
        }
    }

    public static final class Builder {

        private Long id;
        private Instant dateStart;
        private Instant dateEnd;
        private Float distance;
        private Integer stationStart;
        private Integer stationEnd;
        private String bikeRfid;
        private BicycleType bicycleType;
        private String geometry;
        private Integer numberOfVertices;

        private Builder() { }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withDateStart(Instant dateStart) {
            this.dateStart = dateStart;
            return this;
        }

        public Builder withDateEnd(Instant dateEnd) {
            this.dateEnd = dateEnd;
            return this;
        }

        public Builder withDistance(Float distance) {
            this.distance = distance;
            return this;
        }

        public Builder withStationStart(Integer stationStart) {
            this.stationStart = stationStart;
            return this;
        }

        public Builder withStationEnd(Integer stationEnd) {
            this.stationEnd = stationEnd;
            return this;
        }

        public Builder withBikeRfid(String bikeRfid) {
            this.bikeRfid = bikeRfid;
            return this;
        }

        public Builder withBicycleType(BicycleType bicycleType) {
            this.bicycleType = bicycleType;
            return this;
        }

        public Builder withGeometry(String geometry) {
            this.geometry = geometry;
            return this;
        }

        public Builder withNumberOfVertices(Integer numberOfVertices) {
            this.numberOfVertices = numberOfVertices;
            return this;
        }

        public GiraTravelsDataModel build() {
            return new GiraTravelsDataModel(id, dateStart, dateEnd, distance, stationStart, stationEnd, bikeRfid, bicycleType, geometry, numberOfVertices);
        }
    }
}

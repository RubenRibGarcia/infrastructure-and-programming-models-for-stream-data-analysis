package org.isel.thesis.impads.flink.topology.sourcemodel.gira;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.IRMQQueue;
import org.isel.thesis.impads.giragen.datamodel.api.gira.adapter.GiraTravelsDataAdapter;

import java.time.Instant;

public class GiraTravelsSourceModel implements GiraTravelsDataAdapter {

    public static final IRMQQueue QUEUE = IRMQQueue.RMQQueueNaming.withName("gira_travels");

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

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("date_start")
    public Instant getDateStart() {
        return dateStart;
    }

    @JsonProperty("date_start")
    public void setDateStart(Instant dateStart) {
        this.dateStart = dateStart;
    }

    @JsonProperty("date_end")
    public Instant getDateEnd() {
        return dateEnd;
    }

    @JsonProperty("date_end")
    public void setDateEnd(Instant dateEnd) {
        this.dateEnd = dateEnd;
    }

    @JsonProperty("distance")
    public Float getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(Float distance) {
        this.distance = distance;
    }

    @JsonProperty("station_start")
    public Integer getStationStart() {
        return stationStart;
    }

    @JsonProperty("station_start")
    public void setStationStart(Integer stationStart) {
        this.stationStart = stationStart;
    }

    @JsonProperty("station_end")
    public Integer getStationEnd() {
        return stationEnd;
    }

    @JsonProperty("station_end")
    public void setStationEnd(Integer stationEnd) {
        this.stationEnd = stationEnd;
    }

    @JsonProperty("bike_rfid")
    public String getBikeRfid() {
        return bikeRfid;
    }

    @JsonProperty("bike_rfid")
    public void setBikeRfid(String bikeRfid) {
        this.bikeRfid = bikeRfid;
    }

    @JsonProperty("bicycle_type")
    public GiraTravelsDataAdapter.BicycleType getBicycleType() {
        return bicycleType;
    }

    @JsonProperty("bicycle_type")
    public void setBicycleType(BicycleType bicycleType) {
        this.bicycleType = bicycleType;
    }

    @JsonProperty("geom")
    public String getGeometry() {
        return this.geometry;
    }

    @JsonProperty("geom")
    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    @JsonProperty("num_vertices")
    public Integer getNumberOfVertices() {
        return numberOfVertices;
    }

    @JsonProperty("num_vertices")
    public void setNumberOfVertices(Integer numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
    }
}

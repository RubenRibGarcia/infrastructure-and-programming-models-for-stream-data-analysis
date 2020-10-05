package org.isel.thesis.impads.flink.topology.models;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.IRMQQueue;
import org.isel.thesis.impads.giragen.datamodel.api.waze.adapter.WazeJamsDataAdapter;

import java.time.Instant;

public class WazeJamsSourceModel implements WazeJamsDataAdapter {

    public static final IRMQQueue QUEUE = IRMQQueue.RMQQueueNaming.withName("waze_jams");

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

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("level")
    public Integer getLevel() {
        return level;
    }

    @JsonProperty("level")
    public void setLevel(Integer level) {
        this.level = level;
    }

    @JsonProperty("length")
    public Integer getLength() {
        return length;
    }

    @JsonProperty("length")
    public void setLength(Integer length) {
        this.length = length;
    }

    @JsonProperty("type")
    public WazeJamsDataAdapter.JamIntensityType getJamIntensityType() {
        return jamIntensityType;
    }

    @JsonProperty("type")
    public void setJamIntensityType(WazeJamsDataAdapter.JamIntensityType jamIntensityType) {
        this.jamIntensityType = jamIntensityType;
    }

    @JsonProperty("uuid")
    public Long getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("end_node")
    public String getEndNode() {
        return endNode;
    }

    @JsonProperty("end_node")
    public void setEndNode(String endNode) {
        this.endNode = endNode;
    }

    @JsonProperty("speed")
    public Float getSpeed() {
        return speed;
    }

    @JsonProperty("speed")
    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    @JsonProperty("road_type")
    public Integer getRoadType() {
        return roadType;
    }

    @JsonProperty("road_type")
    public void setRoadType(Integer roadType) {
        this.roadType = roadType;
    }

    @JsonProperty("delay")
    public Integer getDelay() {
        return delay;
    }

    @JsonProperty("delay")
    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @JsonProperty("street")
    public String getStreet() {
        return street;
    }

    @JsonProperty("street")
    public void setStreet(String street) {
        this.street = street;
    }

    @JsonProperty("pub_millis")
    public Long getPubMillis() {
        return pubMillis;
    }

    @JsonProperty("pub_millis")
    public void setPubMillis(Long pubMillis) {
        this.pubMillis = pubMillis;
    }

    @JsonProperty("creation_date")
    public Instant getCreationDate() {
        return creationDate;
    }

    @JsonProperty("creation_date")
    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    @JsonProperty("last_mod_date")
    public Instant getLastModDate() {
        return lastModDate;
    }

    @JsonProperty("last_mod_date")
    public void setLastModDate(Instant lastModDate) {
        this.lastModDate = lastModDate;
    }

    @JsonProperty("geom")
    public String getGeometry() {
        return this.geometry;
    }

    @JsonProperty("geom")
    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

}

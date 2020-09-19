package org.isel.thesis.impads.flink.topology.sourcemodel.waze;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.IRMQQueue;
import org.isel.thesis.impads.giragen.datamodel.api.waze.adapter.WazeIrregularitiesDataAdapter;

import java.time.Instant;

public class WazeIrregularitiesSourceModel implements WazeIrregularitiesDataAdapter {

    public static final IRMQQueue QUEUE = IRMQQueue.RMQQueueNaming.withName("waze_irregularities");

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

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("n_thumbs_up")
    public Integer getnThumbsUp() {
        return nThumbsUp;
    }

    @JsonProperty("n_thumbs_up")
    public void setnThumbsUp(Integer nThumbsUp) {
        this.nThumbsUp = nThumbsUp;
    }

    @JsonProperty("update_date")
    public Instant getUpdateDate() {
        return updateDate;
    }

    @JsonProperty("update_date")
    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    @JsonProperty("trend")
    public Integer getTrend() {
        return trend;
    }

    @JsonProperty("trend")
    public void setTrend(Integer trend) {
        this.trend = trend;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("detection_date_millis")
    public Long getDetectionDateMillis() {
        return detectionDateMillis;
    }

    @JsonProperty("detection_date_millis")
    public void setDetectionDateMillis(Long detectionDateMillis) {
        this.detectionDateMillis = detectionDateMillis;
    }

    @JsonProperty("type")
    public WazeIrregularitiesDataAdapter.IrregularityIntensityType getIrregularityIntensityType() {
        return irregularityIntensityType;
    }

    @JsonProperty("type")
    public void setIrregularityIntensityType(WazeIrregularitiesDataAdapter.IrregularityIntensityType irregularityIntensityType) {
        this.irregularityIntensityType = irregularityIntensityType;
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

    @JsonProperty("seconds")
    public Integer getSeconds() {
        return seconds;
    }

    @JsonProperty("seconds")
    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    @JsonProperty("start_node")
    public String getStartNode() {
        return startNode;
    }

    @JsonProperty("start_node")
    public void setStartNode(String startNode) {
        this.startNode = startNode;
    }

    @JsonProperty("street")
    public String getStreet() {
        return street;
    }

    @JsonProperty("street")
    public void setStreet(String street) {
        this.street = street;
    }

    @JsonProperty("jam_level")
    public Integer getJamLevel() {
        return jamLevel;
    }

    @JsonProperty("jam_level")
    public void setJamLevel(Integer jamLevel) {
        this.jamLevel = jamLevel;
    }

    @JsonProperty("waze_id")
    public Long getWazeId() {
        return wazeId;
    }

    @JsonProperty("waze_id")
    public void setWazeId(Long wazeId) {
        this.wazeId = wazeId;
    }

    @JsonProperty("is_highway")
    public Boolean isHighway() {
        return highway;
    }

    @JsonProperty("is_highway")
    public void setIsHighway(Boolean isHighway) {
        this.highway = isHighway;
    }

    @JsonProperty("delay_seconds")
    public Integer getDelaySeconds() {
        return delaySeconds;
    }

    @JsonProperty("delay_seconds")
    public void setDelaySeconds(Integer delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    @JsonProperty("severity")
    public Integer getSeverity() {
        return severity;
    }

    @JsonProperty("severity")
    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    @JsonProperty("alerts_count")
    public Integer getAlertsCount() {
        return alertsCount;
    }

    @JsonProperty("alerts_count")
    public void setAlertsCount(Integer alertsCount) {
        this.alertsCount = alertsCount;
    }

    @JsonProperty("length")
    public Integer getLength() {
        return length;
    }

    @JsonProperty("length")
    public void setLength(Integer length) {
        this.length = length;
    }

    @JsonProperty("update_date_millis")
    public Long getUpdateDateMillis() {
        return updateDateMillis;
    }

    @JsonProperty("update_date_millis")
    public void setUpdateDateMillis(Long updateDateMillis) {
        this.updateDateMillis = updateDateMillis;
    }

    @JsonProperty("detection_date")
    public Instant getDetectionDate() {
        return detectionDate;
    }

    @JsonProperty("detection_date")
    public void setDetectionDate(Instant detectionDate) {
        this.detectionDate = detectionDate;
    }

    @JsonProperty("regular_speed")
    public Float getRegularSpeed() {
        return regularSpeed;
    }

    @JsonProperty("regular_speed")
    public void setRegularSpeed(Float regularSpeed) {
        this.regularSpeed = regularSpeed;
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

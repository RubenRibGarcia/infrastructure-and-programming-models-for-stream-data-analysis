package org.isel.thesis.impads.storm.streams.topology.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.storm.streams.operations.mappers.TupleValueMapper;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.giragen.datamodel.api.waze.adapter.WazeIrregularitiesDataAdapter;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.IJsonTuple;

import java.io.Serializable;
import java.time.Instant;

public class WazeIrregularitiesSourceModel implements WazeIrregularitiesDataAdapter, IJsonTuple, Serializable {

    private static final long serialVersionUID = 1L;

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

    @Override
    public Values getTupleValues() {
        return new Values(id, nThumbsUp, updateDate, trend, city, detectionDateMillis, irregularityIntensityType, endNode, speed, seconds, startNode, street, jamLevel, wazeId, highway, delaySeconds, severity, alertsCount, length, updateDateMillis, detectionDate, regularSpeed, creationDate, lastModDate, geometry);
    }

    public static Fields getTupleField() {
        return new Fields("id", "n_thumbs_up", "update_date", "trend", "city", "detection_date_millis", "type", "end_node", "speed", "seconds", "start_node", "street", "jam_level", "waze_id", "is_highway", "delay_seconds", "severity", "alerts_count", "length", "update_date_millis", "detection_date", "regular_speed", "creation_date", "last_mod_date", "geometry");
    }

    public static final class WazeIrregularitiesTupleMapper implements TupleValueMapper<Observable<WazeIrregularitiesSourceModel>> {

        public static WazeIrregularitiesTupleMapper map() {
            return new WazeIrregularitiesTupleMapper();
        }

        @Override
        public Observable<WazeIrregularitiesSourceModel> apply(Tuple tuple) {
            WazeIrregularitiesSourceModel rvalue = new WazeIrregularitiesSourceModel();
            rvalue.setId((Long)tuple.getValueByField("id"));
            rvalue.setnThumbsUp((Integer)tuple.getValueByField("n_thumbs_up"));
            rvalue.setUpdateDate((Instant)tuple.getValueByField("update_date"));
            rvalue.setTrend((Integer)tuple.getValueByField("trend"));
            rvalue.setCity((String)tuple.getValueByField("city"));
            rvalue.setDetectionDateMillis((Long)tuple.getValueByField("detection_date_millis"));
            rvalue.setIrregularityIntensityType((IrregularityIntensityType)tuple.getValueByField("type"));
            rvalue.setEndNode((String)tuple.getValueByField("end_node"));
            rvalue.setSpeed((Float)tuple.getValueByField("speed"));
            rvalue.setSeconds((Integer)tuple.getValueByField("seconds"));
            rvalue.setStartNode((String)tuple.getValueByField("start_node"));
            rvalue.setStreet((String)tuple.getValueByField("street"));
            rvalue.setJamLevel((Integer)tuple.getValueByField("jam_level"));
            rvalue.setWazeId((Long)tuple.getValueByField("waze_id"));
            rvalue.setIsHighway((Boolean)tuple.getValueByField("is_highway"));
            rvalue.setDelaySeconds((Integer)tuple.getValueByField("delay_seconds"));
            rvalue.setSeverity((Integer)tuple.getValueByField("severity"));
            rvalue.setAlertsCount((Integer)tuple.getValueByField("alerts_count"));
            rvalue.setLength((Integer)tuple.getValueByField("length"));
            rvalue.setUpdateDateMillis((Long)tuple.getValueByField("update_date_millis"));
            rvalue.setDetectionDate((Instant)tuple.getValueByField("detection_date"));
            rvalue.setRegularSpeed((Float)tuple.getValueByField("regular_speed"));
            rvalue.setCreationDate((Instant)tuple.getValueByField("creation_date"));
            rvalue.setLastModDate((Instant)tuple.getValueByField("last_mod_date"));
            rvalue.setGeometry((String)tuple.getValueByField("geometry"));

            return Observable.of(rvalue, rvalue.getDetectionDateMillis(), Instant.now().toEpochMilli());
        }
    }
}

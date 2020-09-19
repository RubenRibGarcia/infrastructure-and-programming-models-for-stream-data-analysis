package org.isel.thesis.impads.storm.sourcemodel.gira;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.storm.streams.operations.mappers.TupleValueMapper;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.giragen.datamodel.api.gira.adapter.GiraTravelsDataAdapter;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.metrics.ObservableImpl;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.IJsonTuple;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.IRabbitMQQueue;

import java.io.Serializable;
import java.time.Instant;

public class GiraTravelsSourceModel implements GiraTravelsDataAdapter, IJsonTuple, Serializable {

    private static final long serialVersionUID = 1L;

    public static final IRabbitMQQueue QUEUE = IRabbitMQQueue.RabbitMQQueueNaming.withName("gira_travels");

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

    @Override
    public Values getTupleValues() {
        return new Values(id, dateStart, dateEnd, distance, stationStart, stationEnd, bikeRfid, bicycleType, geometry, numberOfVertices);
    }

    public static Fields getTupleField() {
        return new Fields("id", "date_start", "date_end", "distance", "station_start", "station_end", "bike_rfid", "bicycle_type", "geometry", "num_vertices");
    }

    public static final class GiraTravelsTupleMapper implements TupleValueMapper<Observable<GiraTravelsSourceModel>> {

        public static GiraTravelsTupleMapper map() {
            return new GiraTravelsTupleMapper();
        }

        @Override
        public Observable<GiraTravelsSourceModel> apply(Tuple tuple) {
            GiraTravelsSourceModel rvalue = new GiraTravelsSourceModel();
            rvalue.setId((Long)tuple.getValueByField("id"));
            rvalue.setDateStart((Instant)tuple.getValueByField("date_start"));
            rvalue.setDateEnd((Instant)tuple.getValueByField("date_end"));
            rvalue.setDistance((Float)tuple.getValueByField("distance"));
            rvalue.setStationStart((Integer)tuple.getValueByField("station_start"));
            rvalue.setStationEnd((Integer)tuple.getValueByField("station_end"));
            rvalue.setBikeRfid((String) tuple.getValueByField("bike_rfid"));
            rvalue.setBicycleType((BicycleType)tuple.getValueByField("bicycle_type"));
            rvalue.setGeometry((String)tuple.getValueByField("geometry"));
            rvalue.setNumberOfVertices((Integer) tuple.getValueByField("num_vertices"));

            return ObservableImpl.of(rvalue, rvalue.getDateStart().toEpochMilli(), Instant.now().toEpochMilli());
        }
    }
}

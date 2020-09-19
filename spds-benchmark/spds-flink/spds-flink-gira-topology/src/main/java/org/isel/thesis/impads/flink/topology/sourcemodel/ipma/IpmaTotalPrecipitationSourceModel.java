package org.isel.thesis.impads.flink.topology.sourcemodel.ipma;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.isel.thesis.impads.flink.rabbitmq.connector.api.IRMQQueue;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.adapter.IpmaTotalPrecipitationDataAdapter;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStationValue;

import java.util.Set;

@JsonIgnoreProperties(value = {"type"})
public class IpmaTotalPrecipitationSourceModel implements IpmaTotalPrecipitationDataAdapter {

    public static final IRMQQueue QUEUE = IRMQQueue.RMQQueueNaming.withName("ipma_total_precipitation");

    private int year;
    private int month;
    private int day;
    private int hour;
    private Set<IpmaStationValue<Float>> ipmaStationsValues;

    @JsonProperty("year")
    public int getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(int year) {
        this.year = year;
    }

    @JsonProperty("month")
    public int getMonth() {
        return month;
    }

    @JsonProperty("month")
    public void setMonth(int month) {
        this.month = month;
    }

    @JsonProperty("day")
    public int getDay() {
        return day;
    }

    @JsonProperty
    public void setDay(int day) {
        this.day = day;
    }

    @JsonProperty("hour")
    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @JsonProperty("values")
    public Set<IpmaStationValue<Float>> getStationsValues() {
        return ipmaStationsValues;
    }

    @JsonProperty("values")
    public void setStationsValues(Set<IpmaStationValue<Float>> ipmaStationsValues) {
        this.ipmaStationsValues = ipmaStationsValues;
    }
}

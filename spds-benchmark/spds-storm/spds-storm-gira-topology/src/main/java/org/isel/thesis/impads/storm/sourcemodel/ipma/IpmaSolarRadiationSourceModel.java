package org.isel.thesis.impads.storm.sourcemodel.ipma;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.storm.streams.operations.mappers.TupleValueMapper;
import org.apache.storm.tuple.Tuple;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.adapter.IpmaSolarRadiationDataAdapter;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStationValue;
import org.isel.thesis.impads.storm.spouts.rabbitmq.api.IRabbitMQQueue;

import java.util.Set;

@JsonIgnoreProperties(value = {"type"})
public class IpmaSolarRadiationSourceModel implements IpmaSolarRadiationDataAdapter {

    public static final IRabbitMQQueue QUEUE = IRabbitMQQueue.RabbitMQQueueNaming.withName("ipma_solar_radiation");

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

    public static final class IpmaSolarRadiationTupleMapper implements TupleValueMapper<IpmaSolarRadiationSourceModel> {

        public static IpmaSolarRadiationTupleMapper map() {
            return new IpmaSolarRadiationTupleMapper();
        }

        @Override
        public IpmaSolarRadiationSourceModel apply(Tuple tuple) {
            return (IpmaSolarRadiationSourceModel) tuple.getValueByField("json");
        }
    }
}

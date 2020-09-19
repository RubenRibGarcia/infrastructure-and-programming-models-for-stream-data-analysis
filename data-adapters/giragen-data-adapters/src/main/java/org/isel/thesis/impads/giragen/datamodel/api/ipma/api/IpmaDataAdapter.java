package org.isel.thesis.impads.giragen.datamodel.api.ipma.api;

import java.util.HashSet;
import java.util.Set;

public interface IpmaDataAdapter<T extends Number> {

    IpmaDataType getDataType();

    int getYear();

    int getMonth();

    int getDay();

    int getHour();

    Set<IpmaStationValue<T>> getStationsValues();

    abstract class AbstractIpmaDataBuilder<M extends IpmaDataAdapter<T>, T extends Number> {

        protected int year;
        protected int month;
        protected int day;
        protected int hour;
        protected Set<IpmaStationValue<T>> stationsValues;

        protected AbstractIpmaDataBuilder() {
            this.stationsValues = new HashSet<>();
        }

        public AbstractIpmaDataBuilder<M, T> withYear(int year) {
            this.year = year;
            return this;
        }

        public AbstractIpmaDataBuilder<M, T> withMonth(int month) {
            this.month = month;
            return this;
        }

        public AbstractIpmaDataBuilder<M, T> withDay(int day) {
            this.day = day;
            return this;
        }

        public AbstractIpmaDataBuilder<M,T> withHour(int hour) {
            this.hour = hour;
            return this;
        }

        public AbstractIpmaDataBuilder<M,T> addIpmaStationValue(IpmaStationValue<T> stationValue) {
            this.stationsValues.add(stationValue);
            return this;
        }

        public abstract M build();
    }
}

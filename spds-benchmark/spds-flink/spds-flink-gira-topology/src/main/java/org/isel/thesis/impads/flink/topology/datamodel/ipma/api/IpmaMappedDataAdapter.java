package org.isel.thesis.impads.flink.topology.datamodel.ipma.api;

import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaDataAdapter;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaDataType;

import java.time.Instant;

public interface IpmaMappedDataAdapter<T extends Number> {

    IpmaDataType getDataType();

    Instant getInstant();

    IpmaMediumStationValues<T> getMediumStationsValue();

    abstract class AbstractIpmaMappedDataBuilder<M extends IpmaDataAdapter<T>, T extends Number> {

        protected Instant instant;
        protected IpmaMediumStationValues<T> mediumStationsValue;

        protected AbstractIpmaMappedDataBuilder() { }

        public AbstractIpmaMappedDataBuilder<M, T> withInstant(Instant instant) {
            this.instant = instant;
            return this;
        }

        public AbstractIpmaMappedDataBuilder<M,T> addIpmaStationValue(IpmaMediumStationValues<T> mediumStationsValues) {
            this.mediumStationsValue = mediumStationsValues;
            return this;
        }

        public abstract M build();
    }
}

package org.isel.thesis.impads.flink.topology.datamodel.ipma;

import org.isel.thesis.impads.flink.topology.datamodel.ipma.api.IpmaMappedDataAdapter;
import org.isel.thesis.impads.flink.topology.datamodel.ipma.api.IpmaMediumStationValues;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaDataType;

import java.time.Instant;

public class IpmaSolarRadiationMappedDataModel implements IpmaMappedDataAdapter<Float> {

    private final Instant instant;
    private final IpmaMediumStationValues<Float> ipmaMediumStationValue;

    public IpmaSolarRadiationMappedDataModel(Instant instant
            , IpmaMediumStationValues<Float> ipmaMediumStationValue) {
        this.instant = instant;
        this.ipmaMediumStationValue = ipmaMediumStationValue;
    }

    @Override
    public IpmaDataType getDataType() {
        return IpmaDataType.SolarRadiation;
    }

    @Override
    public Instant getInstant() {
        return instant;
    }

    @Override
    public IpmaMediumStationValues<Float> getMediumStationsValue() {
        return ipmaMediumStationValue;
    }
}

package org.isel.thesis.impads.flink.topology.datamodel.ipma;

import org.isel.thesis.impads.flink.topology.datamodel.ipma.api.IpmaMappedDataAdapter;
import org.isel.thesis.impads.flink.topology.datamodel.ipma.api.IpmaMediumStationValues;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaDataType;

import java.time.Instant;

public class IpmaMediumWindIntensityMappedDataModel implements IpmaMappedDataAdapter<Float> {

    private final Instant instant;
    private final IpmaMediumStationValues<Float> ipmaMediumStationValue;

    public IpmaMediumWindIntensityMappedDataModel(Instant instant
            , IpmaMediumStationValues<Float> ipmaMediumStationValue) {
        this.instant = instant;
        this.ipmaMediumStationValue = ipmaMediumStationValue;
    }

    @Override
    public IpmaDataType getDataType() {
        return IpmaDataType.MediumWindIntensity;
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

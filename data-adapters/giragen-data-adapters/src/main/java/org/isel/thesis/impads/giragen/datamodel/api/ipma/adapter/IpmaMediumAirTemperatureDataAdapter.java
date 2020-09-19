package org.isel.thesis.impads.giragen.datamodel.api.ipma.adapter;

import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaDataAdapter;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaDataType;

public interface IpmaMediumAirTemperatureDataAdapter extends IpmaDataAdapter<Float> {

    @Override
    default IpmaDataType getDataType() {
        return IpmaDataType.MediumAirTemperature;
    }
}

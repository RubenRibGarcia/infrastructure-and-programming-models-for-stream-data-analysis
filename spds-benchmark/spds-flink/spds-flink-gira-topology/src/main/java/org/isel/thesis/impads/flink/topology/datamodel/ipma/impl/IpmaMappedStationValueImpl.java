package org.isel.thesis.impads.flink.topology.datamodel.ipma.impl;

import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStationValue;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStations;

public class IpmaMappedStationValueImpl<T extends Number> implements IpmaStationValue<T> {

    private IpmaStations ipmaStation;
    private T value;

    public IpmaMappedStationValueImpl(IpmaStations ipmaStation
            , T value) {
        this.ipmaStation = ipmaStation;
        this.value = value;
    }

    public IpmaStations getStation() {
        return ipmaStation;
    }

    public T getValue() {
        return value;
    }
}

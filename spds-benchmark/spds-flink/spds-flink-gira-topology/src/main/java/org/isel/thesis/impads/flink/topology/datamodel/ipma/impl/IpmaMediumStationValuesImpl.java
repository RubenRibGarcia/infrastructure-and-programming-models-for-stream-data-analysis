package org.isel.thesis.impads.flink.topology.datamodel.ipma.impl;

import org.isel.thesis.impads.flink.topology.datamodel.ipma.api.IpmaMediumStationValues;

public class IpmaMediumStationValuesImpl<T extends Number> implements IpmaMediumStationValues<T> {

    public static final Float NON_VALUE = -990f;

    private final T value;

    public IpmaMediumStationValuesImpl(T value) {
        this.value = value;
    }

    @Override
    public T getMediumStationValues() {
        return value;
    }
}

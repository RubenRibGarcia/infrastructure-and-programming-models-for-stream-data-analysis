package org.isel.thesis.impads.giragen.datamodel.api.ipma.api;

public interface IpmaStationValue<T extends Number> {

    IpmaStations getStation();
    T getValue();
}

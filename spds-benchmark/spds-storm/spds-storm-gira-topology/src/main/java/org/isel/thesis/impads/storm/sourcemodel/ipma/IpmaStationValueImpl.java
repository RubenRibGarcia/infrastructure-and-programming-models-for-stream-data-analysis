package org.isel.thesis.impads.storm.sourcemodel.ipma;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStationValue;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStations;

public class IpmaStationValueImpl<T extends Number> implements IpmaStationValue<T> {

    private IpmaStations ipmaStation;
    private T value;

    @JsonProperty("station")
    public IpmaStations getStation() {
        return ipmaStation;
    }

    @JsonProperty("station")
    public void setStation(IpmaStations ipmaStation) {
        this.ipmaStation = ipmaStation;
    }

    @JsonProperty("value")
    public T getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(T value) {
        this.value = value;
    }
}

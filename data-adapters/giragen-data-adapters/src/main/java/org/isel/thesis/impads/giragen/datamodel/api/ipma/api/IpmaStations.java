package org.isel.thesis.impads.giragen.datamodel.api.ipma.api;

public enum IpmaStations {
    Station535("STATION_535"),
    Station579("STATION_579"),
    Station762("STATION_762");

    private final String station;

    IpmaStations(String station) {
        this.station = station;
    }

    public String getStation() {
        return station;
    }

    @Override
    public String toString() {
        return station;
    }
}

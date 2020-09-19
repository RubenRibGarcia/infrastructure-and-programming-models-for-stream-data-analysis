package org.isel.thesis.impads.giragen.datamodel.api.ipma.api;

public enum IpmaDataType {

    MediumAirHumidity("MEDIUM_AIR_HUMIDITY"),
    MediumAirTemperature("MEDIUM_AIR_TEMPERATURE"),
    MediumWindDirection("MEDIUM_WIND_DIRECTION"),
    MediumWindIntensity("MEDIUM_WIND_INTENSITY"),
    SolarRadiation("SOLAR_RADIATION"),
    TotalPrecitipation("TOTAL PRECIPITATION");

    private final String type;

    IpmaDataType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}

package org.isel.thesis.impads.kafka.stream.topology.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.isel.thesis.impads.metrics.api.Observable;

public class GiraTravelsWithWazeResult {

    private final Observable<SimplifiedGiraTravelsModel> giraData;
    private final Observable<SimplifiedWazeJamsModel> wazeJamData;
    private final Observable<SimplifiedWazeIrregularitiesModel> wazeIrregularityData;
    private final boolean giraTravelIntersectsWazeJam;
    private final boolean giraTravelIntersectsWazeIrregularity;
    private final boolean wazeJamAndWazeIrregularityMatches;

    private final float airHumidity;
    private final float airTemperature;
    private final float windDirection;
    private final float windIntensity;
    private final float solarRadiation;
    private final float totalPrecipitation;

    public GiraTravelsWithWazeResult(Observable<SimplifiedGiraTravelsModel> giraData
            , Observable<SimplifiedWazeJamsModel> wazeJamData
            , Observable<SimplifiedWazeIrregularitiesModel> wazeIrregularityData
            , boolean giraTravelIntersectsWazeJam
            , boolean giraTravelIntersectsWazeIrregularity
            , boolean wazeJamAndWazeIrregularityMatches) {
        this.giraData = giraData;
        this.wazeJamData = wazeJamData;
        this.wazeIrregularityData = wazeIrregularityData;
        this.giraTravelIntersectsWazeJam = giraTravelIntersectsWazeJam;
        this.giraTravelIntersectsWazeIrregularity = giraTravelIntersectsWazeIrregularity;
        this.wazeJamAndWazeIrregularityMatches = wazeJamAndWazeIrregularityMatches;
        this.airHumidity = 0.0f;
        this.airTemperature = 0.0f;
        this.windDirection = 0.0f;
        this.windIntensity = 0.0f;
        this.solarRadiation = 0.0f;
        this.totalPrecipitation = 0.0f;
    }

    @JsonProperty("gira_data")
    public Observable<SimplifiedGiraTravelsModel> getGiraData() {
        return giraData;
    }

    @JsonProperty("waze_jam_data")
    public Observable<SimplifiedWazeJamsModel> getWazeJamData() {
        return wazeJamData;
    }

    @JsonProperty("waze_irregularity_data")
    public Observable<SimplifiedWazeIrregularitiesModel> getWazeIrregularityData() {
        return wazeIrregularityData;
    }

    @JsonProperty("gira_travel_intersects_waze_jam")
    public boolean isGiraTravelIntersectsWazeJam() {
        return giraTravelIntersectsWazeJam;
    }

    @JsonProperty("gira_travel_intersects_waze_irregularity")
    public boolean isGiraTravelIntersectsWazeIrregularity() {
        return giraTravelIntersectsWazeIrregularity;
    }

    @JsonProperty("waze_jam_and_waze_irregulity_matches")
    public boolean isWazeJamAndWazeIrregularityMatches() {
        return wazeJamAndWazeIrregularityMatches;
    }

    @JsonProperty("air_humidity")
    public float getAirHumidity() {
        return airHumidity;
    }

    @JsonProperty("air_temperature")
    public float getAirTemperature() {
        return airTemperature;
    }

    @JsonProperty("wind_direction")
    public float getWindDirection() {
        return windDirection;
    }

    @JsonProperty("wind_intensity")
    public float getWindIntensity() {
        return windIntensity;
    }

    @JsonProperty("solar_radiation")
    public float getSolarRadiation() {
        return solarRadiation;
    }

    @JsonProperty("total_precipitation")
    public float getTotalPrecipitation() {
        return totalPrecipitation;
    }
}

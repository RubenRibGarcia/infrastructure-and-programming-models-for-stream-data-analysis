package org.isel.thesis.impads.storm.streams.topology.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GiraTravelsWithWazeAndIpmaResult {

    private final SimplifiedGiraTravelsModel giraData;
    private final SimplifiedWazeJamsModel wazeJamData;
    private final SimplifiedWazeIrregularitiesModel wazeIrregularityData;
    private final IpmaValuesModel ipmaData;
    private final boolean giraTravelIntersectsWazeJam;
    private final boolean giraTravelIntersectsWazeIrregularity;
    private final boolean wazeJamAndWazeIrregularityMatches;

    public GiraTravelsWithWazeAndIpmaResult(SimplifiedGiraTravelsModel giraData
            , SimplifiedWazeJamsModel wazeJamData
            , SimplifiedWazeIrregularitiesModel wazeIrregularityData
            , IpmaValuesModel ipmaData
            , boolean giraTravelIntersectsWazeJam
            , boolean giraTravelIntersectsWazeIrregularity
            , boolean wazeJamAndWazeIrregularityMatches) {
        this.giraData = giraData;
        this.wazeJamData = wazeJamData;
        this.wazeIrregularityData = wazeIrregularityData;
        this.ipmaData = ipmaData;
        this.giraTravelIntersectsWazeJam = giraTravelIntersectsWazeJam;
        this.giraTravelIntersectsWazeIrregularity = giraTravelIntersectsWazeIrregularity;
        this.wazeJamAndWazeIrregularityMatches = wazeJamAndWazeIrregularityMatches;
    }

    @JsonProperty("gira_data")
    public SimplifiedGiraTravelsModel getGiraData() {
        return giraData;
    }

    @JsonProperty("waze_jam_data")
    public SimplifiedWazeJamsModel getWazeJamData() {
        return wazeJamData;
    }

    @JsonProperty("waze_irregularity_data")
    public SimplifiedWazeIrregularitiesModel getWazeIrregularityData() {
        return wazeIrregularityData;
    }

    @JsonProperty("ipma_data")
    public IpmaValuesModel getIpmaValuesData() {
        return ipmaData;
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
}

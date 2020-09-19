package org.isel.thesis.impads.storm.topology;

public final class TopologyConstants {

    public static final class Streams {
        public static final String GIRA_TRAVELS_STREAM = "gira_travels_stream";
        public static final String WAZE_JAMS_STREAM = "waze_jams_streams";
        public static final String WAZE_IRREGULARITIES = "waze_irregularities_streams";
        public static final String JOIN_GIRA_TRAVELS_WITH_WAZE_JAMS_STREAM = "join_gira_travels_with_waze_jams_stream";
    }

    public static final class Spouts {
        public static final String GIRA_TRAVELS_SPOUT = "gira_travels_spout";
        public static final String WAZE_IRREGULARITIES_SPOUT = "waze_irregularities_spout";
        public static final String WAZE_JAMS_SPOUT = "waze_jams_spout";
    }

    public static final class Bolts {
        public static final String FILTER_AND_MAP_GIRA_TRAVELS = "filter_and_map_gira_travels";
        public static final String FILTER_AND_MAP_WAZE_JAMS = "filter_and_map_waze_jams";
        public static final String FILTER_AND_MAP_WAZE_IRREGULARITIES = "filter_and_map_waze_irregularities";
        public static final String JOIN_GIRA_TRAVELS_WITH_WAZE_JAMS = "join_gira_travels_with_waze_jams";
    }
}

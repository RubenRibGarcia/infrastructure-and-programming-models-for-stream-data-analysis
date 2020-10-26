package org.isel.thesis.impads.kafka.stream.topology.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.isel.thesis.impads.kafka.stream.serdes.JsonSerdes;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeJams;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableGiraTravelsWithWazeResults;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeJamsModel;

public final class SerdesUtils {

    public static JsonSerdes<ObservableSimplifiedGiraTravelsModel> simplifiedGiraTravelsSerdes(ObjectMapper mapper) {
        return JsonSerdes.newJsonSerders(mapper, ObservableSimplifiedGiraTravelsModel.class);
    }

    public static JsonSerdes<ObservableSimplifiedWazeJamsModel> simplifiedWazeJamsSerdes(ObjectMapper mapper) {
        return JsonSerdes.newJsonSerders(mapper, ObservableSimplifiedWazeJamsModel.class);
    }

    public static JsonSerdes<ObservableSimplifiedWazeIrregularitiesModel> simplifiedWazeIrregularitiesSerdes(ObjectMapper mapper) {
        return JsonSerdes.newJsonSerders(mapper, ObservableSimplifiedWazeIrregularitiesModel.class);
    }

    public static JsonSerdes<ObservableJoinedGiraTravelsWithWazeJams> joinedGiraTravelsWithWazeJamsJsonSerdes(ObjectMapper mapper) {
        return JsonSerdes.newJsonSerders(mapper, ObservableJoinedGiraTravelsWithWazeJams.class);
    }

    public static JsonSerdes<ObservableGiraTravelsWithWazeResults> giraTravelsWithWazeResultsJsonSerdes(ObjectMapper mapper) {
        return JsonSerdes.newJsonSerders(mapper, ObservableGiraTravelsWithWazeResults.class);
    }
}

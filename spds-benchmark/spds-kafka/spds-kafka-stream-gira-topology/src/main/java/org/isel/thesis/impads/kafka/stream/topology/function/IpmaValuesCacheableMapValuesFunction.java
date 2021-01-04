package org.isel.thesis.impads.kafka.stream.topology.function;

import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;
import org.isel.thesis.impads.kafka.stream.connectors.redis.KafkaStreamRedisHashCacheableMapValuesFunction;
import org.isel.thesis.impads.kafka.stream.topology.model.IpmaValuesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWaze;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeAndIpma;
import org.isel.thesis.impads.kafka.stream.topology.model.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.structures.Tuple4;

public class IpmaValuesCacheableMapValuesFunction
        extends KafkaStreamRedisHashCacheableMapValuesFunction<ObservableJoinedGiraTravelsWithWaze, ObservableJoinedGiraTravelsWithWazeAndIpma, SimplifiedGiraTravelsModel, IpmaValuesModel> {

    public IpmaValuesCacheableMapValuesFunction(RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> function) {
        super(function);
    }

    @Override
    public ObservableJoinedGiraTravelsWithWazeAndIpma apply(ObservableJoinedGiraTravelsWithWaze v) {

        IpmaValuesModel rvalue = function.map(v.getData().getFirst());

        return new ObservableJoinedGiraTravelsWithWazeAndIpma(
                v.map(Tuple4.of(v.getData().getFirst(), v.getData().getSecond(), v.getData().getThird(), rvalue)));
    }
}

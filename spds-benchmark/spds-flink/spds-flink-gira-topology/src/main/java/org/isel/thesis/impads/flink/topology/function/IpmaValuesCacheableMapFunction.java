package org.isel.thesis.impads.flink.topology.function;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;
import org.isel.thesis.impads.flink.connectors.redis.functions.FlinkRedisHashCacheableMapFunction;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.Observable;

public class IpmaValuesCacheableMapFunction
    extends FlinkRedisHashCacheableMapFunction<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>
        , Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>
        , SimplifiedGiraTravelsModel
        , IpmaValuesModel> {

    private static final long serialVersionUID = 1L;

    private IpmaValuesCacheableMapFunction(RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> function) {
        super(function);
    }

    public static IpmaValuesCacheableMapFunction function(RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> function) {
        return new IpmaValuesCacheableMapFunction(function);
    }

    @Override
    public Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> map(
            Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> tuple) throws Exception {

        IpmaValuesModel rvalue = function.map(tuple.getData().f0);

        return tuple.map(Tuple4.of(tuple.getData().f0, tuple.getData().f1, tuple.getData().f2, rvalue));
    }
}

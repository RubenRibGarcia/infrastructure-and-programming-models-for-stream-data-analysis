package org.isel.thesis.impads.flink.topology.function;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.metrics.Observable;

public class RedisIpmaValuesMapFunction
        extends RichMapFunction<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>
                , Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> {

    private RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> redisHashCacheableMapFunction;

    private RedisIpmaValuesMapFunction(RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> redisHashCacheableMapFunction) {
        this.redisHashCacheableMapFunction = redisHashCacheableMapFunction;
    }

    public static RedisIpmaValuesMapFunction map(RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> redisHashCacheableMapFunction) {
        return new RedisIpmaValuesMapFunction(redisHashCacheableMapFunction);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.redisHashCacheableMapFunction.open();
        super.open(parameters);
    }

    @Override
    public Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> map(
            Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> tuple)
            throws Exception {

        IpmaValuesModel rvalue = redisHashCacheableMapFunction.apply(tuple.getData().f0);

        return tuple.map(Tuple4.of(tuple.getData().f0, tuple.getData().f1, tuple.getData().f2, rvalue));
    }

    @Override
    public void close() throws Exception {
        if (redisHashCacheableMapFunction != null) {
            redisHashCacheableMapFunction.close();
        }
        super.close();
    }
}

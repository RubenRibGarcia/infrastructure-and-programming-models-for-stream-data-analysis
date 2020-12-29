package org.isel.thesis.impads.flink.topology.function;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableLoaderFunction;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.flink.topology.utils.IpmaUtils;
import org.isel.thesis.impads.metrics.Observable;

import java.time.Instant;

public class RedisIpmaValuesMapFunction
        extends RichMapFunction<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>
                , Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> {

    private RedisHashCacheableLoaderFunction<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>
            , Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> redisHashCacheableLoaderFunction;

    public RedisIpmaValuesMapFunction(RedisHashCacheableLoaderFunction<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>
            , Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> redisHashCacheableLoaderFunction) {
        this.redisHashCacheableLoaderFunction = redisHashCacheableLoaderFunction;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.redisHashCacheableLoaderFunction.open();
        super.open(parameters);
    }

    @Override
    public Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>> map(
            Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> tuple)
            throws Exception {
        //TODO: Review this
        redisHashCacheableLoaderFunction.load()

        final IpmaValuesModel rvalue;
        String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(tuple.getData().f0.getEventTimestamp()));
        rvalue = IpmaValuesModel.fetchAndAddFromRedis(hashField, redisCommandsContainer);

        return tuple.map(Tuple4.of(tuple.getData().f0, tuple.getData().f1, tuple.getData().f2, rvalue));
    }

    @Override
    public void close() throws Exception {
        if (redisHashCacheableLoaderFunction != null) {
            redisHashCacheableLoaderFunction.close();
        }
        super.close();
    }
}

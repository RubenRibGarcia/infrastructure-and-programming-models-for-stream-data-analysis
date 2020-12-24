package org.isel.thesis.impads.flink.topology.function;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.connectors.redis.RedisProcessFunction;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.util.Collector;
import org.isel.thesis.impads.flink.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.flink.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.flink.topology.utils.IpmaUtils;
import org.isel.thesis.impads.metrics.Observable;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class CacheableIpmaValues
        extends RedisProcessFunction<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>
        , Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> {

    private Map<String, IpmaValuesModel> cache;

    public CacheableIpmaValues(FlinkJedisConfigBase flinkJedisConfigBase) {
        super(flinkJedisConfigBase);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.cache = new LinkedHashMap<>();
    }

    @Override
    public void processElement(Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> tuple
            , Context context
            , Collector<Observable<Tuple4<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel, IpmaValuesModel>>> collector) throws Exception {

        IpmaValuesModel rvalue;
        String hashField = IpmaUtils.instantToHashField(Instant.ofEpochMilli(tuple.getData().f0.getEventTimestamp()));
        if (cache.containsKey(hashField)) {
            rvalue = cache.get(hashField);
        }
        else {
            rvalue = IpmaValuesModel.fetchAndAddFromRedis(hashField, redisCommandsContainer);
            cache.putIfAbsent(hashField, rvalue);
        }

        collector.collect(tuple.map(Tuple4.of(tuple.getData().f0, tuple.getData().f1, tuple.getData().f2, rvalue)));
    }
}
package org.isel.thesis.impads.storm.topology.bolts;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.isel.thesis.impads.connectors.redis.RedisHashCacheableMapFunction;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.connectors.redis.StormRedisHashCacheableMapperBolt;
import org.isel.thesis.impads.storm.connectors.redis.TupleMapper;
import org.isel.thesis.impads.storm.topology.models.IpmaValuesModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedGiraTravelsModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.storm.topology.models.SimplifiedWazeJamsModel;
import org.isel.thesis.impads.structures.Tuple3;
import org.isel.thesis.impads.structures.Tuple4;

public class IpmaValuesCacheableMapBolt extends StormRedisHashCacheableMapperBolt<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>
        , SimplifiedGiraTravelsModel
        , IpmaValuesModel> {

    private static final long serialVersionUID = 1L;

    public IpmaValuesCacheableMapBolt(RedisHashCacheableMapFunction<SimplifiedGiraTravelsModel, IpmaValuesModel> function
            , TupleMapper<Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>>> tupleMapper
            , String... outputFields) {
        super(function, tupleMapper, outputFields);
    }

    @Override
    public void execute(Tuple tuple) {

        Observable<Tuple3<SimplifiedGiraTravelsModel, SimplifiedWazeJamsModel, SimplifiedWazeIrregularitiesModel>> data =
                tupleMapper.apply(tuple);

        IpmaValuesModel rvalue = function.map(data.getData().getFirst());

        outputCollector.emit(new Values(data.map(Tuple4.of(data.getData().getFirst(), data.getData().getSecond(), data.getData().getThird(), rvalue))));
    }
}
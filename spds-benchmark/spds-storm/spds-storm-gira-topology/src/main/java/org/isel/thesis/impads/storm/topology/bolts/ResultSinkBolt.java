package org.isel.thesis.impads.storm.topology.bolts;


import org.apache.storm.tuple.Tuple;
import org.isel.thesis.impads.connectors.redis.RedisWriterFunction;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.connectors.redis.StormRedisSinkBolt;
import org.isel.thesis.impads.storm.connectors.redis.TupleMapper;
import org.isel.thesis.impads.storm.topology.models.GiraTravelsWithWazeAndIpmaResult;

public class ResultSinkBolt extends StormRedisSinkBolt<Observable<GiraTravelsWithWazeAndIpmaResult>> {

    public ResultSinkBolt(TupleMapper<Observable<GiraTravelsWithWazeAndIpmaResult>> tupleMapper
            , RedisWriterFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> writerFunction) {
        super(tupleMapper, writerFunction);
    }

    @Override
    public void execute(Tuple tuple) {
        Observable<GiraTravelsWithWazeAndIpmaResult> result = tupleMapper.apply(tuple);
        writerFunction.write(result);
    }
}

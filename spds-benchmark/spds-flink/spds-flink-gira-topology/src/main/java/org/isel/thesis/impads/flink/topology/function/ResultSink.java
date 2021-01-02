package org.isel.thesis.impads.flink.topology.function;

import org.isel.thesis.impads.connectors.redis.RedisWriterFunction;
import org.isel.thesis.impads.flink.connectors.redis.functions.FlinkRedisSinkFunction;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.metrics.Observable;

public class ResultSink extends FlinkRedisSinkFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> {

    private static final long serialVersionUID = 1L;

    private ResultSink(RedisWriterFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> writerFunction) {
        super(writerFunction);
    }

    public static ResultSink sink(RedisWriterFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> writerFunction) {
        return new ResultSink(writerFunction);
    }

    @Override
    public void invoke(Observable<GiraTravelsWithWazeAndIpmaResult> input, Context context)  {
        writerFunction.write(input);
    }
}

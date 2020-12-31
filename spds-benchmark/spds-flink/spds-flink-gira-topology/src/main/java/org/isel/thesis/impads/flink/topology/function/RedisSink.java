package org.isel.thesis.impads.flink.topology.function;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.isel.thesis.impads.connectors.redis.RedisWriterFunction;
import org.isel.thesis.impads.flink.topology.models.GiraTravelsWithWazeAndIpmaResult;
import org.isel.thesis.impads.metrics.Observable;

public class RedisSink extends RichSinkFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> {

    private static final long serialVersionUID = 1L;

    private RedisWriterFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> writerFunction;

    private RedisSink(RedisWriterFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> writerFunction) {
        this.writerFunction = writerFunction;
    }

    public static RedisSink sink(RedisWriterFunction<Observable<GiraTravelsWithWazeAndIpmaResult>> writerFunction) {
        return new RedisSink(writerFunction);
    }

    @Override
    public void invoke(Observable<GiraTravelsWithWazeAndIpmaResult> input, Context context) throws Exception {
        writerFunction.accept(input);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        writerFunction.open();
    }

    @Override
    public void close() throws Exception {
        if (writerFunction != null) {
            writerFunction.close();
        }
    }
}

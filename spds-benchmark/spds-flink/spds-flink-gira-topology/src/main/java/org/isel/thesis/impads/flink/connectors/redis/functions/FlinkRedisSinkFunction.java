package org.isel.thesis.impads.flink.connectors.redis.functions;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.isel.thesis.impads.connectors.redis.RedisWriterFunction;

public abstract class FlinkRedisSinkFunction<IN>
        extends RichSinkFunction<IN> {

    private static final long serialVersionUID = 1L;

    protected RedisWriterFunction<IN> writerFunction;

    public FlinkRedisSinkFunction() { }

    protected FlinkRedisSinkFunction(RedisWriterFunction<IN> writerFunction) {
        this.writerFunction = writerFunction;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.writerFunction.open();
    }

    @Override
    public void close() throws Exception {
        if (writerFunction != null) {
            writerFunction.close();
        }
        super.close();
    }
}

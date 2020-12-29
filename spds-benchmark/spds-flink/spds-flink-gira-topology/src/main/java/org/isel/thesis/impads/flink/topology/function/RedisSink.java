package org.isel.thesis.impads.flink.topology.function;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.isel.thesis.impads.connectors.redis.RedisSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisSink<IN> extends RichSinkFunction<IN> {

    private static final long serialVersionUID = 1L;

    private RedisSinkFunction<IN> sinkFunction;

    private RedisSink(RedisSinkFunction<IN> sinkFunction) {
        this.sinkFunction = sinkFunction;
    }

    public static <IN> RedisSink<IN> sink(RedisSinkFunction<IN> sinkFunction) {
        return new RedisSink<>(sinkFunction);
    }

    @Override
    public void invoke(IN input, Context context) throws Exception {
        sinkFunction.sink(input);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        sinkFunction.open();
    }

    @Override
    public void close() throws Exception {
        if (sinkFunction != null) {
            sinkFunction.close();
        }
    }
}

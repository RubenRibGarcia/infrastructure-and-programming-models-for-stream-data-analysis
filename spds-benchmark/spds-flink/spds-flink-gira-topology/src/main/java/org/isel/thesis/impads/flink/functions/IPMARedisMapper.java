package org.isel.thesis.impads.flink.functions;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.guava18.com.google.common.base.Optional;

public class IPMARedisMapper extends AbstractRedisMapper<Tuple2<String, String>, Optional<String>> {

    public IPMARedisMapper(String host
            , int port) {
        super(host, port);
    }

    @Override
    public Optional<String> map(Tuple2<String, String> ipmaDataKeyPair) throws Exception {
        return Optional.fromNullable(jedis.hget(ipmaDataKeyPair.f0, ipmaDataKeyPair.f1));
    }
}

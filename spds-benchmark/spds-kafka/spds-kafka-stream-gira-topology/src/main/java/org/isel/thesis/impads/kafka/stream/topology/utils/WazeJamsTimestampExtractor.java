package org.isel.thesis.impads.kafka.stream.topology.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.isel.thesis.impads.kafka.stream.topology.model.WazeJamsSourceModel;

public class WazeJamsTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long l) {
        WazeJamsSourceModel giraTravelModel = (WazeJamsSourceModel) consumerRecord.value();
        return giraTravelModel.getPubMillis();
    }
}

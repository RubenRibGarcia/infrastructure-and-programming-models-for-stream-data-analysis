package org.isel.thesis.impads.kafka.stream.topology.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.isel.thesis.impads.kafka.stream.topology.sourcemodel.waze.WazeIrregularitiesSourceModel;

public class WazeIrregularitiesTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long l) {
        WazeIrregularitiesSourceModel wazeIrregularitiesSourceModel = (WazeIrregularitiesSourceModel) consumerRecord.value();
        return wazeIrregularitiesSourceModel.getDetectionDateMillis();
    }
}

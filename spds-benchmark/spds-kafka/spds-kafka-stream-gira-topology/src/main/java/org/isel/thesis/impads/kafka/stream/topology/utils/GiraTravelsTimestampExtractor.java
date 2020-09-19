package org.isel.thesis.impads.kafka.stream.topology.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.isel.thesis.impads.kafka.stream.topology.sourcemodel.gira.GiraTravelsSourceModel;

public class GiraTravelsTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long l) {
        GiraTravelsSourceModel giraTravelModel = (GiraTravelsSourceModel) consumerRecord.value();
        return giraTravelModel.getDateStart().toEpochMilli();
    }
}

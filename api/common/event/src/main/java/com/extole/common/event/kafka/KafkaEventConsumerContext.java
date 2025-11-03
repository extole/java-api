package com.extole.common.event.kafka;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.TopicPartition;

public final class KafkaEventConsumerContext {

    private final Map<TopicPartition, PartitionOffsets> partitionOffsets;

    public KafkaEventConsumerContext(Map<TopicPartition, PartitionOffsets> partitionOffsets) {
        this.partitionOffsets = ImmutableMap.copyOf(partitionOffsets);
    }

    public Map<TopicPartition, PartitionOffsets> getPartitionOffsets() {
        return partitionOffsets;
    }
}

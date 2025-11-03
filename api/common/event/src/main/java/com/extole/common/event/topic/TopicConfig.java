package com.extole.common.event.topic;

import java.time.Duration;
import java.util.Optional;

import com.extole.common.event.Topic;

public interface TopicConfig {

    int getAttempt();

    Topic getTopic();

    Duration getRetryInterval();

    long getBatchSize();

    Topic getOnFailureTopic();

    Optional<Integer> getDesiredPartitionCount();

    Optional<Short> getDesiredReplicationFactor();

    Optional<Long> getLogRetentionMs();

    Optional<Long> getPartitionRetentionBytes();
}

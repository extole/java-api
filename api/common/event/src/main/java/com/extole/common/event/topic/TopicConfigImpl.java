package com.extole.common.event.topic;

import java.time.Duration;
import java.util.Optional;

import com.extole.common.event.Topic;
import com.extole.common.lang.ToString;

public class TopicConfigImpl implements TopicConfig {
    private final int attempt;
    private final Topic topic;
    private final Duration retryInterval;
    private final long batchSize;
    private final Topic onFailureTopic;
    private final Optional<Integer> desiredPartitionCount;
    private final Optional<Short> desiredReplicationFactor;
    private final Optional<Long> logRetentionMs;
    private final Optional<Long> partitionRetentionBytes;

    public TopicConfigImpl(int attempt,
        Topic topic,
        Duration retryInterval,
        long batchSize,
        Topic onFailureTopic,
        Integer desiredPartitionCount,
        Short desiredReplicationFactor,
        Optional<Long> logRetentionMs,
        Optional<Long> partitionRetentionBytes) {
        this.attempt = attempt;
        this.topic = topic;
        this.retryInterval = retryInterval;
        this.batchSize = batchSize;
        this.onFailureTopic = onFailureTopic;
        this.desiredPartitionCount = Optional.ofNullable(desiredPartitionCount);
        this.desiredReplicationFactor = Optional.ofNullable(desiredReplicationFactor);
        this.logRetentionMs = logRetentionMs;
        this.partitionRetentionBytes = partitionRetentionBytes;
    }

    @Override
    public int getAttempt() {
        return attempt;
    }

    @Override
    public Topic getTopic() {
        return topic;
    }

    @Override
    public Duration getRetryInterval() {
        return retryInterval;
    }

    @Override
    public long getBatchSize() {
        return batchSize;
    }

    @Override
    public Topic getOnFailureTopic() {
        return onFailureTopic;
    }

    @Override
    public Optional<Integer> getDesiredPartitionCount() {
        return desiredPartitionCount;
    }

    @Override
    public Optional<Short> getDesiredReplicationFactor() {
        return desiredReplicationFactor;
    }

    @Override
    public Optional<Long> getLogRetentionMs() {
        return logRetentionMs;
    }

    @Override
    public Optional<Long> getPartitionRetentionBytes() {
        return partitionRetentionBytes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

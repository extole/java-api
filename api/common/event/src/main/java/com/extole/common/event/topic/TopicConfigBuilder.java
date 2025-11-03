package com.extole.common.event.topic;

import java.time.Duration;
import java.util.Optional;

import com.extole.common.event.Topic;
import com.extole.common.lang.ToString;

public class TopicConfigBuilder {
    private int attempt;
    private Topic topic;
    private Duration retryInterval;
    private long batchSize;
    private Topic onFailureTopic;
    private Integer desiredPartitionCount;
    private Short desiredReplicationFactor;
    private Optional<Long> logRetentionMs = Optional.empty();
    private Optional<Long> partitionRetentionBytes = Optional.empty();

    TopicConfigBuilder() {
    }

    public TopicConfigBuilder(TopicConfig sourceTopicConfig) {
        this.attempt = sourceTopicConfig.getAttempt();
        this.topic = sourceTopicConfig.getTopic();
        this.retryInterval = sourceTopicConfig.getRetryInterval();
        this.batchSize = sourceTopicConfig.getBatchSize();
        this.onFailureTopic = sourceTopicConfig.getOnFailureTopic();
        this.desiredPartitionCount = sourceTopicConfig.getDesiredPartitionCount().orElse(null);
        this.desiredReplicationFactor = sourceTopicConfig.getDesiredReplicationFactor().orElse(null);
        this.logRetentionMs = sourceTopicConfig.getLogRetentionMs();
        this.partitionRetentionBytes = sourceTopicConfig.getPartitionRetentionBytes();
    }

    public TopicConfigBuilder withAttempt(int attempt) {
        this.attempt = attempt;
        return this;
    }

    public TopicConfigBuilder withTopic(Topic topic) {
        this.topic = topic;
        return this;
    }

    public TopicConfigBuilder withRetryInterval(Duration retryInterval) {
        this.retryInterval = retryInterval;
        return this;
    }

    public TopicConfigBuilder withBatchSize(long batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    TopicConfigBuilder withOnFailureTopic(Topic onFailureTopic) {
        this.onFailureTopic = onFailureTopic;
        return this;
    }

    public TopicConfigBuilder withDesiredPartitionCount(Integer desiredPartitionCount) {
        this.desiredPartitionCount = desiredPartitionCount;
        return this;
    }

    public TopicConfigBuilder withDesiredReplicationFactor(Short desiredReplicationFactor) {
        this.desiredReplicationFactor = desiredReplicationFactor;
        return this;
    }

    public TopicConfigBuilder withLogRetentionMs(long logRetentionMs) {
        this.logRetentionMs = Optional.of(logRetentionMs);
        return this;
    }

    public TopicConfigBuilder withPartitionRetentionBytes(long partitionRetentionBytes) {
        this.partitionRetentionBytes = Optional.of(partitionRetentionBytes);
        return this;
    }

    public TopicConfig build() {
        return new TopicConfigImpl(attempt, topic, retryInterval, batchSize, onFailureTopic,
            desiredPartitionCount, desiredReplicationFactor, logRetentionMs, partitionRetentionBytes);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

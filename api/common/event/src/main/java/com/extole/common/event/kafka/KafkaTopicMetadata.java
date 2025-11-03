package com.extole.common.event.kafka;

import static com.extole.common.event.kafka.consumer.KafkaEventConsumerFactory.TOPIC_POSTFIX_DEAD;

import com.extole.common.event.Topic;
import com.extole.common.log.PerformanceLogger;

public class KafkaTopicMetadata {
    private final int attempt;
    private final Topic topic;
    private final int partition;
    private final long offset;
    private final Topic onFailureTopic;
    private final PerformanceLogger performanceLogger;

    public KafkaTopicMetadata(int attempt, Topic topic, int partition, long offset, Topic onFailureTopic,
        PerformanceLogger performanceLogger) {
        this.attempt = attempt;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.onFailureTopic = onFailureTopic;
        this.performanceLogger = performanceLogger;
    }

    public int getAttempt() {
        return attempt;
    }

    public Topic getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public boolean isDead() {
        return topic.getName().endsWith(TOPIC_POSTFIX_DEAD);
    }

    public boolean isLastAttempt() {
        return onFailureTopic.getName().endsWith(TOPIC_POSTFIX_DEAD);
    }

    public PerformanceLogger getPerformanceLogger() {
        return performanceLogger;
    }
}

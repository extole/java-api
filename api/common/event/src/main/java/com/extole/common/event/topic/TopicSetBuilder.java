package com.extole.common.event.topic;

public interface TopicSetBuilder {

    TopicConfigBuilder editAttempt(int attempt);

    TopicSetBuilder setAttemptCount(int attemptCount);

    int getAttemptCount();

    TopicSet build();

}

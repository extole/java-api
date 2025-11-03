package com.extole.common.event.recent;

public class KafkaTopicNotFoundException extends Exception {

    private final String topicName;

    public KafkaTopicNotFoundException(String message,
        String topicName) {
        super(message);
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}

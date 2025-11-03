package com.extole.common.event.recent;

public class KafkaFetchException extends Exception {

    private final String topicName;

    public KafkaFetchException(String message, String topicName, Throwable cause) {
        super(message, cause);
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}

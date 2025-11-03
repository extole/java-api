package com.extole.common.event;

import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;

import com.extole.common.event.kafka.KafkaTopicMetadata;
import com.extole.common.event.topic.TopicConfig;

public interface BatchEventListener<T> extends SmartInitializingSingleton {

    // Throwing KafkaRetryException will cause the Consumer to send all events in the batch to the retry topic
    // topicConfig is passed to allow listener to send individual events to be retried, if desired
    void handleEvents(List<T> events, KafkaTopicMetadata topicMetadata, TopicConfig topicConfig)
        throws KafkaRetryException;

}

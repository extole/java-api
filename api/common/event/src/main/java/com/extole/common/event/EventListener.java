package com.extole.common.event;

import org.springframework.beans.factory.SmartInitializingSingleton;

import com.extole.common.event.kafka.KafkaTopicMetadata;

public interface EventListener<T> extends SmartInitializingSingleton {

    void handleEvent(T event, KafkaTopicMetadata topicMetadata) throws KafkaRetryException, KafkaWaitException;

}

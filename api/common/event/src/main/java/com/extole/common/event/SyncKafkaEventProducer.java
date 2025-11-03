package com.extole.common.event;

import java.time.Instant;

import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.kafka.producer.KafkaSendEventFailureException;
import com.extole.id.Id;
import com.extole.spring.StartFirstStopLast;

public interface SyncKafkaEventProducer extends StartFirstStopLast {

    <T> void sendEvent(T event, Topic topic, Id<?> clientId)
        throws EventTooLargeException, KafkaSendEventFailureException;

    <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId, Instant requestTime)
        throws EventTooLargeException, KafkaSendEventFailureException;

    <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId)
        throws EventTooLargeException, KafkaSendEventFailureException;

}

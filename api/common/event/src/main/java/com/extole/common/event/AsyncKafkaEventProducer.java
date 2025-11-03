package com.extole.common.event;

import java.time.Instant;

import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.id.Id;
import com.extole.spring.StartFirstStopLast;

public interface AsyncKafkaEventProducer extends StartFirstStopLast {

    int getInMemoryEventQueueSize(KafkaClusterType clusterType);

    <T> void sendEvent(T event, Topic topic, Id<?> clientId) throws EventTooLargeException;

    <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId, Instant requestTime)
        throws EventTooLargeException;

    <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId) throws EventTooLargeException;

}

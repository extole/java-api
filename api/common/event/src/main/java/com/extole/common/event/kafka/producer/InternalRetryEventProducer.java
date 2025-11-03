package com.extole.common.event.kafka.producer;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.extole.common.event.Topic;
import com.extole.common.event.kafka.EventTooLargeException;

public interface InternalRetryEventProducer {

    void sendRecord(Topic retryTopic, ConsumerRecord<String, String> recordToRetry, Instant processAfter)
        throws InterruptedException, ExecutionException, EventTooLargeException;

}

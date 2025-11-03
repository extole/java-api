package com.extole.common.event.kafka.producer;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.extole.common.event.PartitionKey;
import com.extole.common.event.SyncKafkaEventProducer;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.topic.TopicService;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.id.Id;

@Component
public class SyncKafkaEventProducerImpl implements SyncKafkaEventProducer, InternalRetryEventProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SyncKafkaEventProducerImpl.class);
    private final VerifiedTopicSyncKafkaEventProducer verifiedTopicSyncKafkaEventProducer;
    private final TopicService topicService;

    public SyncKafkaEventProducerImpl(ApplicationContext applicationContext,
        @Value("${extole.instance.name:lo}") String instanceName,
        SyncKafkaProducerConfig kafkaProducerConfig,
        ExtoleMetricRegistry metricRegistry,
        @Value("${kafka.sync.producer.max.in.flight.events:2000}") int maxInFlightEvents,
        TopicService topicService) {
        this.verifiedTopicSyncKafkaEventProducer =
            new VerifiedTopicSyncKafkaEventProducer(applicationContext.getApplicationName(), instanceName,
                kafkaProducerConfig, metricRegistry, maxInFlightEvents);
        this.topicService = topicService;
    }

    @Override
    public void stop() {
        verifiedTopicSyncKafkaEventProducer.stop();
    }

    @Override
    public <T> void sendEvent(T event, Topic topic, Id<?> clientId)
        throws EventTooLargeException, KafkaSendEventFailureException {
        validateTopic(topic);
        verifiedTopicSyncKafkaEventProducer.sendEvent(event, topic, clientId);
    }

    @Override
    public <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId, Instant requestTime)
        throws EventTooLargeException, KafkaSendEventFailureException {
        validateTopic(topic);
        verifiedTopicSyncKafkaEventProducer.sendEvent(event, topic, partitionKey, clientId, requestTime);
    }

    @Override
    public <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId)
        throws EventTooLargeException, KafkaSendEventFailureException {
        validateTopic(topic);
        verifiedTopicSyncKafkaEventProducer.sendEvent(event, topic, partitionKey, clientId);
    }

    @Override
    public void sendRecord(Topic retryTopic, ConsumerRecord<String, String> recordToRetry, Instant processAfter)
        throws InterruptedException, ExecutionException, EventTooLargeException {
        try {
            validateTopic(retryTopic);
        } catch (KafkaSendEventFailureException e) {
            if (e.getCause() != null && e.getCause() instanceof ExecutionException) {
                throw new ExecutionException(e.getCause());
            } else {
                throw new InterruptedException(e.getMessage());
            }
        }
        verifiedTopicSyncKafkaEventProducer.sendRecord(retryTopic, recordToRetry, processAfter);
    }

    private void validateTopic(Topic topic) throws KafkaSendEventFailureException {
        try {
            InternalKafkaProducer internalProducer = verifiedTopicSyncKafkaEventProducer.getProducer(topic);
            topicService.validateTopic(topic, internalProducer.getBootstrapServers());
        } catch (ExecutionException e) {
            throw new KafkaSendEventFailureException(String.format("Failed to send event to topic %s",
                topic.getName()), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaSendEventFailureException(String.format("Failed to send event to topic %s",
                topic.getName()), e);
        }
    }
}

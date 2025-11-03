package com.extole.common.event.kafka.producer;

import static com.extole.common.event.kafka.KafkaHeaders.HEADER_PROCESS_AFTER;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Strings;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.PartitionKey;
import com.extole.common.event.SyncKafkaEventProducer;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.kafka.KafkaHeaders;
import com.extole.common.lang.date.ExtoleTimeModule;
import com.extole.common.metrics.ExtoleCounter;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.id.Id;

public class VerifiedTopicSyncKafkaEventProducer implements SyncKafkaEventProducer, InternalRetryEventProducer {
    private static final Logger LOG = LoggerFactory.getLogger(VerifiedTopicSyncKafkaEventProducer.class);
    private static final ObjectMapper OBJECT_MAPPER =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new ExtoleTimeModule())
            .registerModule(new Jdk8Module())
            .registerModule(new GuavaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final InternalKafkaProducer producer;
    private final InternalKafkaProducer globalProducer;
    private final ExtoleMetricRegistry metricRegistry;
    private final String producerIdPrefix;
    private final int maxInFlightEvents;
    private final AtomicInteger numberOfEventsInFlight = new AtomicInteger(0);

    public VerifiedTopicSyncKafkaEventProducer(String applicationName,
        String instanceName,
        SyncKafkaProducerConfig kafkaProducerConfig,
        ExtoleMetricRegistry metricRegistry,
        int maxInFlightEvents) {
        String trimmedAppName;
        if (!Strings.isNullOrEmpty(applicationName)) {
            trimmedAppName = applicationName.substring(1);
        } else {
            trimmedAppName = "unspecified";
        }
        this.producerIdPrefix = instanceName + "-" + trimmedAppName;
        this.producer = new InternalKafkaProducer(KafkaClusterType.EDGE, kafkaProducerConfig, metricRegistry,
            VerifiedTopicSyncKafkaEventProducer.class, producerIdPrefix);
        this.globalProducer = new InternalKafkaProducer(KafkaClusterType.GLOBAL, kafkaProducerConfig, metricRegistry,
            VerifiedTopicSyncKafkaEventProducer.class, producerIdPrefix);
        this.metricRegistry = metricRegistry;
        this.maxInFlightEvents = maxInFlightEvents;
    }

    @Override
    public <T> void sendEvent(T event, Topic topic, Id<?> clientId)
        throws EventTooLargeException, KafkaSendEventFailureException {
        send(event, topic, null, clientId, Instant.now());
    }

    @Override
    public <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId, Instant requestTime)
        throws EventTooLargeException, KafkaSendEventFailureException {
        send(event, topic, partitionKey, clientId, requestTime);
    }

    @Override
    public <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId)
        throws EventTooLargeException, KafkaSendEventFailureException {
        send(event, topic, partitionKey, clientId, Instant.now());
    }

    @Override
    public void sendRecord(Topic topic, ConsumerRecord<String, String> record, Instant processAfter)
        throws InterruptedException, ExecutionException {
        Headers newHeaders = new RecordHeaders(record.headers().toArray()).remove(HEADER_PROCESS_AFTER);
        addProcessAfterHeader(newHeaders, processAfter);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic.getName(),
            null, Long.valueOf(Instant.now().toEpochMilli()), record.key(), record.value(), newHeaders);
        send(topic, producerRecord);
    }

    @Override
    public void stop() {
        LOG.info("shutting down sync kafka producer");
        producer.shutdown();
        globalProducer.shutdown();
        LOG.info("sync producer has been shutdown.");
    }

    private <T> void send(T event, Topic topic, @Nullable PartitionKey partitionKey, Id<?> clientId,
        Instant requestTime) throws EventTooLargeException, KafkaSendEventFailureException {
        try {
            if (numberOfEventsInFlight.getAndIncrement() >= maxInFlightEvents) {
                getErrorCounter(topic).increment();
                throw new KafkaSendEventFailureException(String.format("Failed to send event of type %s to topic %s "
                    + "- producer queue is full", event.getClass().getSimpleName(), topic.getName()));
            }
            Headers headers = new RecordHeaders();
            headers.add(KafkaHeaders.HEADER_CLIENT_ID, clientId.getValue().getBytes());
            headers.add(KafkaHeaders.HEADER_REQUEST_TIME, String.valueOf(requestTime).getBytes());
            try {
                headers.add(KafkaHeaders.HEADER_PRODUCER_ID, producerIdPrefix.getBytes());
                String eventAsString =
                    event instanceof String ? (String) event : OBJECT_MAPPER.writeValueAsString(event);
                ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(topic.getName(), null, Long.valueOf(Instant.now().toEpochMilli()),
                        partitionKey != null ? partitionKey.getValue() : null, eventAsString, headers);
                getProducer(topic).checkDataLength(producerRecord, topic);
                send(topic, producerRecord);
            } catch (JsonProcessingException | ExecutionException e) {
                throw new KafkaSendEventFailureException(String.format("Failed to send event of type %s to topic %s",
                    event.getClass().getSimpleName(), topic.getName()), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new KafkaSendEventFailureException(String.format("Failed to send event of type %s to topic %s",
                    event.getClass().getSimpleName(), topic.getName()), e);
            }
        } finally {
            numberOfEventsInFlight.decrementAndGet();
        }
    }

    private void send(Topic topic,
        ProducerRecord<String, String> producerRecord)
        throws InterruptedException, ExecutionException {
        InternalKafkaProducer internalProducer = getProducer(topic);
        internalProducer.send(producerRecord, topic).get();
    }

    InternalKafkaProducer getProducer(Topic topic) {
        if (KafkaClusterType.EDGE == topic.getClusterType()) {
            return producer;
        } else {
            return globalProducer;
        }
    }

    private void addProcessAfterHeader(Headers headers, Instant processAfter) {
        try {
            headers.add(HEADER_PROCESS_AFTER, OBJECT_MAPPER.writeValueAsBytes(processAfter));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to add processAfter header {} for event", processAfter, e);
        }
    }

    private ExtoleCounter getErrorCounter(Topic topic) {
        return metricRegistry
            .counter(VerifiedTopicSyncKafkaEventProducer.class.getName() + "." + topic.getName() + ".error");
    }
}

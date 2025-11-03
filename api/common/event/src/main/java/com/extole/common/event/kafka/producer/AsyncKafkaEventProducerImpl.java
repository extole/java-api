package com.extole.common.event.kafka.producer;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Strings;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.extole.common.event.AsyncKafkaEventProducer;
import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.PartitionKey;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.kafka.KafkaHeaders;
import com.extole.common.event.topic.TopicService;
import com.extole.common.lang.ExtoleThreadFactory;
import com.extole.common.lang.date.ExtoleTimeModule;
import com.extole.common.metrics.ExtoleCounter;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.id.Id;

@Component
public class AsyncKafkaEventProducerImpl implements AsyncKafkaEventProducer {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncKafkaEventProducerImpl.class);
    private static final long SHUTDOWN_MS = 30000L;
    private static final long RETRY_BACKOFF_MS = 100L;
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
    private final ExecutorService edgeExecutor;
    private final ExecutorService globalExecutor;
    private final LinkedBlockingQueue<QueuedEvent> edgeEvents;
    private final LinkedBlockingQueue<QueuedEvent> globalEvents;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final String producerIdPrefix;
    private final TopicService topicService;
    private final int dropEventThreshold;
    private final ExtoleMetricRegistry metricRegistry;

    @Autowired
    AsyncKafkaEventProducerImpl(ApplicationContext applicationContext,
        @Value("${extole.instance.name:lo}") String instanceName,
        AsyncKafkaProducerConfig kafkaProducerConfig,
        ExtoleMetricRegistry metricRegistry,
        TopicService topicService,
        @Value("${async.producer.drop.event.threshold:3000}") int dropEventThreshold) {
        String trimmedAppName;
        String applicationName = applicationContext.getApplicationName();
        if (!Strings.isNullOrEmpty(applicationName)) {
            trimmedAppName = applicationName.substring(1);
        } else {
            trimmedAppName = "unspecified";
        }
        this.producerIdPrefix = instanceName + "-" + trimmedAppName;
        this.producer = new InternalKafkaProducer(KafkaClusterType.EDGE, kafkaProducerConfig, metricRegistry,
            AsyncKafkaEventProducerImpl.class, producerIdPrefix);
        this.globalProducer = new InternalKafkaProducer(KafkaClusterType.GLOBAL, kafkaProducerConfig, metricRegistry,
            AsyncKafkaEventProducerImpl.class, producerIdPrefix);
        this.edgeExecutor = Executors
            .newSingleThreadExecutor(
                new ExtoleThreadFactory(AsyncKafkaEventProducerImpl.class.getSimpleName() + "-edge"));
        this.globalExecutor = Executors
            .newSingleThreadExecutor(
                new ExtoleThreadFactory(AsyncKafkaEventProducerImpl.class.getSimpleName() + "-global"));
        this.topicService = topicService;
        this.dropEventThreshold = dropEventThreshold;
        this.edgeEvents = new LinkedBlockingQueue<>(dropEventThreshold);
        this.globalEvents = new LinkedBlockingQueue<>(dropEventThreshold);
        this.metricRegistry = metricRegistry;
    }

    @Override
    @PostConstruct
    public void start() {
        edgeExecutor.execute(() -> sendEvents(edgeEvents));
        globalExecutor.execute(() -> sendEvents(globalEvents));
    }

    @Override
    public void stop() {
        LOG.info("shutting down async kafka producer");
        shutdown.set(true);
        edgeExecutor.shutdown();
        globalExecutor.shutdown();
        try {
            boolean edgeShutdownComplete = edgeExecutor.awaitTermination(SHUTDOWN_MS, TimeUnit.MILLISECONDS);
            if (!edgeShutdownComplete) {
                LOG.error("Failed to wait for edgeProducerExecutor to shutdown after waiting {}ms",
                    String.valueOf(SHUTDOWN_MS));
            }
            boolean globalShutdownComplete = globalExecutor.awaitTermination(SHUTDOWN_MS, TimeUnit.MILLISECONDS);
            if (!globalShutdownComplete) {
                LOG.error("Failed to wait for globalProducerExecutor to shutdown after waiting {}ms",
                    String.valueOf(SHUTDOWN_MS));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Shutdown thread for the Async kafka producer interrupted while waiting for the executors "
                + "to terminate", e);
        }
        producer.shutdown();
        globalProducer.shutdown();
        LOG.info("async producer has been shutdown.");
    }

    @Override
    public int getInMemoryEventQueueSize(KafkaClusterType clusterType) {
        if (KafkaClusterType.EDGE == clusterType) {
            return edgeEvents.size();
        } else {
            return globalEvents.size();
        }
    }

    @Override
    public <T> void sendEvent(T event, Topic topic, Id<?> clientId) throws EventTooLargeException {
        send(event, topic, null, clientId, Instant.now());
    }

    @Override
    public <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId, Instant requestTime)
        throws EventTooLargeException {
        send(event, topic, partitionKey, clientId, requestTime);
    }

    @Override
    public <T> void sendEvent(T event, Topic topic, PartitionKey partitionKey, Id<?> clientId)
        throws EventTooLargeException {
        send(event, topic, partitionKey, clientId, Instant.now());
    }

    private <T> void send(T event, Topic topic, @Nullable PartitionKey partitionKey, Id<?> clientId,
        Instant requestTime) throws EventTooLargeException {
        Headers headers = new RecordHeaders();
        headers.add(KafkaHeaders.HEADER_CLIENT_ID, clientId.getValue().getBytes());
        headers.add(KafkaHeaders.HEADER_REQUEST_TIME, String.valueOf(requestTime).getBytes());
        try {
            headers.add(KafkaHeaders.HEADER_PRODUCER_ID, producerIdPrefix.getBytes());
            String eventAsString = event instanceof String ? (String) event : OBJECT_MAPPER.writeValueAsString(event);
            ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(topic.getName(), null, Long.valueOf(Instant.now().toEpochMilli()),
                    partitionKey != null ? partitionKey.getValue() : null, eventAsString, headers);
            getProducer(topic).checkDataLength(producerRecord, topic);
            addEventToQueue(new QueuedEvent(topic, producerRecord),
                (KafkaClusterType.EDGE == topic.getClusterType() ? edgeEvents : globalEvents));
        } catch (JsonProcessingException e) {
            getErrorCounter(topic).increment();
            throw new KafkaEventProducerRuntimeException(
                String.format("Failed to encode event of type %s to json while "
                    + "attempting to put on topic %s", event.getClass().getSimpleName(), topic.getName()),
                e);
        }
    }

    private void addEventToQueue(QueuedEvent event, LinkedBlockingQueue<QueuedEvent> events) {
        if (!events.offer(event)) {
            getErrorCounter(event.getTopic()).increment();
            LOG.error("Exceeded in memory event queue size {} - dropping event for topic {}", dropEventThreshold,
                event.getTopic());
        }
    }

    private void sendEvents(LinkedBlockingQueue<QueuedEvent> events) {
        while (!shutdown.get() || !events.isEmpty()) {
            try {
                QueuedEvent event = events.poll(RETRY_BACKOFF_MS, TimeUnit.MILLISECONDS);
                if (event != null) {
                    try {
                        InternalKafkaProducer internalProducer = getProducer(event.getTopic());
                        topicService.validateTopic(event.getTopic(), internalProducer.getBootstrapServers());
                        internalProducer.send(event.getRecord(), event.getTopic());
                    } catch (Exception e) {
                        getErrorCounter(event.getTopic()).increment();
                        LOG.error("Failed to produce event to topic {}", event.getTopic(), e);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.warn("Got interrupted while trying record to produce - are we shutting down?={}",
                    Boolean.valueOf(shutdown.get()));
            } catch (Exception e) {
                LOG.error("Failed to get next record to produce", e);
            }
        }
        LOG.info("stopped sending events");
    }

    private InternalKafkaProducer getProducer(Topic topic) {
        if (KafkaClusterType.EDGE == topic.getClusterType()) {
            return producer;
        } else {
            return globalProducer;
        }
    }

    private ExtoleCounter getErrorCounter(Topic topic) {
        return metricRegistry
            .counter(AsyncKafkaEventProducerImpl.class.getName() + "." + topic.getName() + ".error");
    }

    private static final class QueuedEvent {
        private final Topic topic;
        private final ProducerRecord<String, String> record;

        QueuedEvent(Topic topic, ProducerRecord<String, String> record) {
            this.topic = topic;
            this.record = record;
        }

        Topic getTopic() {
            return topic;
        }

        ProducerRecord<String, String> getRecord() {
            return record;
        }
    }
}

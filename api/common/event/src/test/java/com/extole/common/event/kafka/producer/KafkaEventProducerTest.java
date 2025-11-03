package com.extole.common.event.kafka.producer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TimeoutException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.extole.common.event.AsyncKafkaEventProducer;
import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.PartitionKey;
import com.extole.common.event.SyncKafkaEventProducer;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.kafka.KafkaHeaders;
import com.extole.id.Id;

@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {
    "com.extole.common.event.kafka.producer",
})
@TestPropertySource(properties = {
    "kafka.sync.producer.max.in.flight.events=" + KafkaEventProducerTest.IN_FLIGHT_MAX,
    "async.producer.backoff.threshold=" + KafkaEventProducerTest.IN_FLIGHT_MAX
})
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaEventProducerTest {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventProducerTest.class);
    static final int IN_FLIGHT_MAX = 5;

    @Autowired
    private SyncKafkaEventProducer syncProducer;
    @Autowired
    private AsyncKafkaEventProducer asyncProducer;
    @Autowired
    private MockProducer<String, String> edgeSyncProducer;
    @Autowired
    private MockProducer<String, String> globalSyncProducer;
    @Autowired
    private MockProducer<String, String> edgeAsyncProducer;
    @Autowired
    private MockProducer<String, String> globalAsyncProducer;
    @Value("${kafka.producer.max.request.size.bytes:8000000}")
    private Integer maxRequestSizeBytes;

    @Test
    public void testClusterRouting() throws Exception {
        Id<?> clientId = Id.valueOf("100");
        String event = "clusterRouting";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                syncProducer.sendEvent(event, new Topic("edgeTopic", KafkaClusterType.EDGE), clientId);
            } catch (EventTooLargeException | KafkaSendEventFailureException e) {
                throw new RuntimeException("Failed to send event", e);
            }
        });
        await("wait for sync producer to call internal producer")
            .untilAsserted(() -> assertTrue(edgeSyncProducer.completeNext()));
        assertThat(edgeSyncProducer.history().size()).isEqualTo(1);
        assertTrue(globalSyncProducer.history().isEmpty());
        executor.submit(() -> {
            try {
                syncProducer.sendEvent(event, new Topic("globalTopic", KafkaClusterType.GLOBAL), clientId);
            } catch (EventTooLargeException | KafkaSendEventFailureException e) {
                throw new RuntimeException("Failed to send event", e);
            }
        });
        await("wait for sync producer to call internal producer")
            .untilAsserted(() -> assertTrue(globalSyncProducer.completeNext()));
        assertThat(globalSyncProducer.history().size()).isEqualTo(1);
        assertThat(edgeSyncProducer.history().size()).isEqualTo(1);

        asyncProducer.sendEvent(event, new Topic("edgeTopic", KafkaClusterType.EDGE), clientId);

        await("wait for async producer to call internal producer")
            .untilAsserted(() -> assertTrue(edgeAsyncProducer.completeNext()));
        assertThat(edgeAsyncProducer.history().size()).isEqualTo(1);
        assertTrue(globalAsyncProducer.history().isEmpty());

        asyncProducer.sendEvent(event, new Topic("globalTopic", KafkaClusterType.GLOBAL), clientId);

        await("wait for async producer to call internal producer")
            .untilAsserted(() -> assertTrue(globalAsyncProducer.completeNext()));
        assertThat(edgeAsyncProducer.history().size()).isEqualTo(1);
        assertThat(globalAsyncProducer.history().size()).isEqualTo(1);

        executor.shutdown();
        assertTrue(executor.awaitTermination(1L, TimeUnit.SECONDS));
    }

    @Test
    public void testSyncHeaders() throws Exception {
        Id<?> clientId = Id.valueOf("100");
        Instant requestTime = Instant.now().minus(Duration.ofDays(1L));
        PartitionKey partitionKey = new PartitionKey("syncHeaders");
        Topic topic = new Topic("syncHeaders", KafkaClusterType.EDGE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                syncProducer.sendEvent("syncHeaders", topic, new PartitionKey("syncHeaders"), clientId, requestTime);
            } catch (EventTooLargeException | KafkaSendEventFailureException e) {
                LOG.error("Failed to send event", e);
            }
        });
        await("wait for sync producer to call internal producer")
            .untilAsserted(() -> assertTrue(edgeSyncProducer.completeNext()));
        executor.shutdown();
        assertTrue(executor.awaitTermination(1L, TimeUnit.SECONDS));
        assertFalse(globalSyncProducer.completeNext());
        assertThat(edgeSyncProducer.history().size()).isEqualTo(1);
        ProducerRecord<String, String> producedEvent = edgeSyncProducer.history().get(0);
        assertThat(new String(producedEvent.headers().lastHeader(KafkaHeaders.HEADER_CLIENT_ID).value()))
            .isEqualTo(clientId.getValue());
        assertThat(new String(producedEvent.headers().lastHeader(KafkaHeaders.HEADER_REQUEST_TIME).value()))
            .isEqualTo(String.valueOf(requestTime));
        assertThat(producedEvent.key()).isEqualTo(partitionKey.getValue());
        assertThat(producedEvent.topic()).isEqualTo(topic.getName());
    }

    @Test
    public void testSyncEventTooLarge() throws Exception {
        Id<?> clientId = Id.valueOf("100");
        String event = RandomStringUtils.randomAlphabetic(maxRequestSizeBytes + 1);
        try {
            syncProducer.sendEvent(event, new Topic("syncTooLarge", KafkaClusterType.EDGE), clientId);
            fail();
        } catch (EventTooLargeException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testSyncFailsOnInternalError() throws Exception {
        Id<?> clientId = Id.valueOf("100");
        Topic topic = new Topic("syncFailInternal", KafkaClusterType.EDGE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try {
                syncProducer.sendEvent("syncFailInternal", topic, clientId);
            } catch (EventTooLargeException | KafkaSendEventFailureException e) {
                throw new RuntimeException(e);
            }
        });
        await("wait for sync producer to call internal producer")
            .untilAsserted(() -> assertTrue(edgeSyncProducer.errorNext(new TimeoutException())));
        try {
            future.get();
            fail();
        } catch (ExecutionException e) {
            assertTrue(e.getCause().getCause() instanceof KafkaSendEventFailureException);
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(1L, TimeUnit.SECONDS));
    }

    @Test
    public void testSyncFailsImmediatelyOnBurstThresholdExceeded() throws Exception {
        Id<?> clientId = Id.valueOf("100");
        Topic topic = new Topic("syncFailBurst", KafkaClusterType.EDGE);
        List<Future<?>> futures = new ArrayList<>();
        AtomicInteger failures = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(IN_FLIGHT_MAX + 1);
        for (int i = 0; i < IN_FLIGHT_MAX + 1; i++) {
            String event = "event-" + i;
            futures.add(executor.submit(() -> {
                try {
                    LOG.debug("attempting to send event {}", event);
                    syncProducer.sendEvent(event, topic, clientId);
                    LOG.debug("Successfully produced event {}", event);
                } catch (EventTooLargeException | KafkaSendEventFailureException e) {
                    failures.incrementAndGet();
                }
            }));
        }
        await("wait for sync producer to call internal producer")
            .untilAsserted(() -> {
                assertThat(failures.get()).isEqualTo(1);
                assertThat(edgeSyncProducer.history().size()).as("unexpected history " + edgeSyncProducer.history())
                    .isEqualTo(IN_FLIGHT_MAX);
                while (edgeSyncProducer.completeNext()) {
                    // DO NOTHING
                }
            });
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(5L, TimeUnit.SECONDS));
    }

    @Test
    public void testSyncShutdownBlocksUntilEventsInFlightAreSent() throws Exception {
        Id<?> clientId = Id.valueOf("100");
        Topic topic = new Topic("syncShutdown", KafkaClusterType.EDGE);
        String event = "syncShutdown";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AtomicBoolean sendComplete = new AtomicBoolean(false);
        executor.submit(() -> {
            try {
                LOG.debug("attempting to send event {}", event);
                syncProducer.sendEvent(event, topic, clientId);
                sendComplete.set(true);
                LOG.debug("Successfully produced event {}", event);
            } catch (EventTooLargeException | KafkaSendEventFailureException e) {
                throw new RuntimeException(e);
            }
        });
        await("wait for sync producer to offer event")
            .untilAsserted(() -> assertFalse(edgeSyncProducer.history().isEmpty()));
        assertFalse(sendComplete.get());
        syncProducer.stop(() -> LOG.debug("call to shutdown sync producer succeeded"));
        executor.shutdown();
        assertTrue(executor.awaitTermination(5L, TimeUnit.SECONDS));
        assertTrue(sendComplete.get());
    }

    @Test
    public void testAsyncHeaders() throws Exception {
        Id<?> clientId = Id.valueOf("100");
        Instant requestTime = Instant.now().minus(Duration.ofDays(1L));
        PartitionKey partitionKey = new PartitionKey("asyncHeaders");
        Topic topic = new Topic("asyncHeaders", KafkaClusterType.EDGE);
        asyncProducer.sendEvent("asyncHeaders", topic, partitionKey, clientId, requestTime);
        await("wait for async producer to send event")
            .untilAsserted(() -> assertTrue(edgeAsyncProducer.completeNext()));
        assertThat(edgeAsyncProducer.history().size()).isEqualTo(1);
        assertTrue(globalAsyncProducer.history().isEmpty());
        ProducerRecord<String, String> producedEvent = edgeAsyncProducer.history().get(0);
        assertThat(new String(producedEvent.headers().lastHeader(KafkaHeaders.HEADER_CLIENT_ID).value()))
            .isEqualTo(clientId.getValue());
        assertThat(new String(producedEvent.headers().lastHeader(KafkaHeaders.HEADER_REQUEST_TIME).value()))
            .isEqualTo(String.valueOf(requestTime));
        assertThat(producedEvent.key()).isEqualTo(partitionKey.getValue());
        assertThat(producedEvent.topic()).isEqualTo(topic.getName());
    }

    @Test
    public void testAsyncEventTooLarge() {
        Id<?> clientId = Id.valueOf("100");
        String event = RandomStringUtils.randomAlphabetic(maxRequestSizeBytes + 1);
        try {
            asyncProducer.sendEvent(event, new Topic("asyncTooLarge", KafkaClusterType.EDGE), clientId);
            fail();
        } catch (EventTooLargeException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testAsyncSucceedsOnInternalError() throws Exception {
        Id<?> clientId = Id.valueOf("100");
        Topic topic = new Topic("asyncSuccess", KafkaClusterType.EDGE);
        asyncProducer.sendEvent("asyncSuccess", topic, clientId);
        await("wait for async producer to call internal producer")
            .untilAsserted(() -> assertTrue(edgeAsyncProducer.errorNext(new TimeoutException())));
    }

    @Test
    public void testAsyncWillDropEventsOnBurstThresholdExceeded() throws Exception {
        Id<?> clientId = Id.valueOf("100");
        Topic topic = new Topic("asyncDrop", KafkaClusterType.EDGE);
        List<Future<?>> futures = new ArrayList<>();
        int burstCount = IN_FLIGHT_MAX + 1000;
        ExecutorService executor = Executors.newFixedThreadPool(IN_FLIGHT_MAX);
        AtomicInteger failures = new AtomicInteger(0);
        for (int i = 0; i < burstCount; i++) {
            String event = "event-" + i;
            futures.add(executor.submit(() -> {
                try {
                    asyncProducer.sendEvent(event, topic, clientId);
                } catch (EventTooLargeException e) {
                    failures.incrementAndGet();
                }
            }));
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(5L, TimeUnit.SECONDS));
        assertThat(0).isEqualTo(failures.get());
        Assertions.assertThat(burstCount).isGreaterThanOrEqualTo(edgeAsyncProducer.history().size());
        Assertions.assertThat(IN_FLIGHT_MAX).isLessThanOrEqualTo(edgeAsyncProducer.history().size());
    }
}

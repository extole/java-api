package com.extole.common.event.kafka.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.extole.common.event.BatchEventListener;
import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.KafkaTopicMetadata;
import com.extole.common.event.kafka.producer.InternalRetryEventProducer;
import com.extole.common.event.kafka.producer.TestTopicService;
import com.extole.common.event.kafka.serializer.json.JsonDecoder;
import com.extole.common.event.topic.TopicConfig;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.metrics.ExtoleMetricRegistry;

@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {
    "com.extole.common.event.kafka.producer",
})
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaBatchEventConsumerTest {
    private static final Topic TEST_TOPIC = new Topic("test", KafkaClusterType.EDGE);
    private static final long TEST_AWAIT_SECONDS = 30;
    private static final long PROCESSING_TIMEOUT_MS = 10000L;
    private static final int MAX_PARTITIONS = 10;
    private static final int IN_MEMORY_PARTITION_EVENT_MAX = 10;
    private static final long BATCH_WAIT_MAX_MS = 100L;
    private static final long PERFORMANCE_LOGGER_THRESHOLD_MS = 1000L;
    private static final int OFFSET_BOUND = 100000000;

    @Autowired
    private TestTopicService testTopicService;
    @Autowired
    private ExtoleMetricRegistry metricRegistry;
    @Autowired
    private InternalRetryEventProducer retryProducer;
    @Autowired
    private MockProducer<String, String> edgeSyncProducer;
    @Autowired
    private KafkaConsumerRecordProcessor kafkaConsumerRecordProcessor;

    private MockConsumer<String, String> mockConsumer;
    private KafkaEventConsumer<String> consumer;
    private TopicPartition topicPartition;
    private long initialOffset;

    @BeforeEach
    public void setup() {
        mockConsumer = new MockConsumer<>(OffsetResetStrategy.LATEST);
        topicPartition = new TopicPartition(TEST_TOPIC.getName(), 0);
        HashMap<TopicPartition, Long> endOffsets = new HashMap<>();
        initialOffset = new Random().nextInt(OFFSET_BOUND);
        endOffsets.put(topicPartition, Long.valueOf(initialOffset));
        mockConsumer.updateEndOffsets(endOffsets);
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.rebalance(Collections.singletonList(topicPartition));
        });
    }

    @AfterEach
    public void cleanup() {
        consumer.shutdown();
        assertTrue(mockConsumer.closed());
    }

    @Test
    public void testConsumerBatchesEventsFromMultiplePolls() {
        testTopicService.setConfig(TEST_TOPIC, builder -> builder.withBatchSize(2L));
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(new ConsumerRecord<>(TEST_TOPIC.getName(), 0, initialOffset, "test", "\"test1\""));
        });
        TestBatchEventListener batchListener = new TestBatchEventListener();
        long batchWaitMaxMs = 10000L;
        consumer = constructConsumer(mockConsumer, batchListener, PROCESSING_TIMEOUT_MS, batchWaitMaxMs);

        Assertions.assertThat(batchListener.getBatchesProcessed()).isEmpty();

        long secondOffset = initialOffset + 1;
        mockConsumer.schedulePollTask(
            () -> mockConsumer
                .addRecord(new ConsumerRecord<>(TEST_TOPIC.getName(), 0, secondOffset, "test", "\"test2\"")));

        await("wait for batchListener to receive batch of events")
            .untilAsserted(() -> Assertions.assertThat(batchListener.getBatchesProcessed()).isNotEmpty());
        Assertions.assertThat(batchListener.getBatchesProcessed()).size().isEqualTo(1);
        List<String> batch = batchListener.getBatchesProcessed().get(0);
        Assertions.assertThat(batch).size().isEqualTo(2);
        Assertions.assertThat(batch.get(0)).isEqualTo("test1");
        Assertions.assertThat(batch.get(1)).isEqualTo("test2");

        awaitCommittedOffset(secondOffset + 1);
    }

    @Test
    public void testConsumerWaitsMaxBatchTimeThenProcessesAnyways() {
        testTopicService.setConfig(TEST_TOPIC, builder -> builder.withBatchSize(2L));
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(new ConsumerRecord<>(TEST_TOPIC.getName(), 0, initialOffset, "test", "\"test1\""));
        });
        TestBatchEventListener batchListener = new TestBatchEventListener();
        consumer = constructConsumer(mockConsumer, batchListener);

        Assertions.assertThat(batchListener.getBatchesProcessed()).isEmpty();

        await("wait for batchListener to receive batch of events")
            .untilAsserted(() -> Assertions.assertThat(batchListener.getBatchesProcessed()).isNotEmpty());
        Assertions.assertThat(batchListener.getBatchesProcessed()).size().isEqualTo(1);
        List<String> batch = batchListener.getBatchesProcessed().get(0);
        Assertions.assertThat(batch).size().isEqualTo(1);
        Assertions.assertThat(batch.get(0)).isEqualTo("test1");

        awaitCommittedOffset(initialOffset + 1);
    }

    @Test
    public void testBatchEventListenerInterruptedOnTimeout() {
        testTopicService.setConfig(TEST_TOPIC, builder -> builder.withBatchSize(2L));
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(new ConsumerRecord<>(TEST_TOPIC.getName(), 0, initialOffset, "test", "\"test1\""));
        });
        long processingTimeoutMs = 100L;
        AtomicBoolean isInterrupted = new AtomicBoolean(false);
        TestBatchEventListener batchListener = new TestBatchEventListener((events) -> {
            try {
                Thread.sleep(processingTimeoutMs * 2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isInterrupted.set(true);
            }
        });
        consumer = constructConsumer(mockConsumer, batchListener, processingTimeoutMs, BATCH_WAIT_MAX_MS);

        long secondOffset = initialOffset + 1;
        mockConsumer.schedulePollTask(
            () -> mockConsumer
                .addRecord(new ConsumerRecord<>(TEST_TOPIC.getName(), 0, secondOffset, "test", "\"test2\"")));

        await("wait for batchListener to receive batch of events")
            .untilAsserted(() -> Assertions.assertThat(batchListener.getBatchesProcessed()).isNotEmpty());
        Assertions.assertThat(batchListener.getBatchesProcessed()).size().isEqualTo(1);
        List<String> batch = batchListener.getBatchesProcessed().get(0);
        Assertions.assertThat(batch).size().isEqualTo(2);
        Assertions.assertThat(batch.get(0)).isEqualTo("test1");
        Assertions.assertThat(batch.get(1)).isEqualTo("test2");
        Assertions.assertThat(isInterrupted).isTrue();

        await("wait for sync producer to call internal producer")
            .untilAsserted(() -> assertTrue(edgeSyncProducer.completeNext()));
        await("wait for sync producer to call internal producer a second time")
            .untilAsserted(() -> assertTrue(edgeSyncProducer.completeNext()));
        assertThat(edgeSyncProducer.history().size()).isEqualTo(2);

        awaitCommittedOffset(secondOffset + 1);

    }

    @Test
    public void testConsumerProducesWholeBatchToRetryTopicOnFailure() {
        testTopicService.setConfig(TEST_TOPIC, builder -> builder.withBatchSize(2L));
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(new ConsumerRecord<>(TEST_TOPIC.getName(), 0, initialOffset, "test", "\"test1\""));
        });
        TestBatchEventListener listener = new TestBatchEventListener((events) -> {
            throw new RuntimeException();
        });
        consumer = constructConsumer(mockConsumer, listener);

        long secondOffset = initialOffset + 1;
        mockConsumer.schedulePollTask(
            () -> mockConsumer
                .addRecord(new ConsumerRecord<>(TEST_TOPIC.getName(), 0, secondOffset, "test", "\"test2\"")));

        await("wait for sync producer to call internal producer")
            .untilAsserted(() -> assertTrue(edgeSyncProducer.completeNext()));
        await("wait for sync producer to call internal producer a second time")
            .untilAsserted(() -> assertTrue(edgeSyncProducer.completeNext()));
        List<ProducerRecord<String, String>> producerRecords = edgeSyncProducer.history();
        assertThat(producerRecords.size()).isEqualTo(2);
        Assertions.assertThat(producerRecords).allMatch(
            record -> record.topic().equals(testTopicService.getConfig(TEST_TOPIC).getOnFailureTopic().getName()));
        Assertions.assertThat(listener.getBatchesProcessed()).isEmpty();

        awaitCommittedOffset(secondOffset + 1);
    }

    @Test
    public void testConsumerDoesNotCommitOffsetForBatchOnRetryProductionFailure() {
        testTopicService.setConfig(TEST_TOPIC, builder -> builder.withBatchSize(2L));
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(new ConsumerRecord<>(TEST_TOPIC.getName(), 0, initialOffset, "test", "\"test1\""));
        });
        AtomicInteger attemptCount = new AtomicInteger(0);
        TestBatchEventListener listener = new TestBatchEventListener((events) -> {
            attemptCount.getAndIncrement();
            throw new RuntimeException();
        });
        consumer = constructConsumer(mockConsumer, listener);

        long secondOffset = initialOffset + 1;
        mockConsumer.schedulePollTask(
            () -> mockConsumer
                .addRecord(new ConsumerRecord<>(TEST_TOPIC.getName(), 0, secondOffset, "test", "\"test2\"")));

        await("wait for sync producer to call internal producer")
            .untilAsserted(() -> {
                Assertions.assertThat(attemptCount.get()).isEqualTo(1);
                assertTrue(edgeSyncProducer.errorNext(new RuntimeException()));
            });
        Assertions.assertThat(listener.getBatchesProcessed()).isEmpty();

        await("wait for sync producer to call internal producer")
            .untilAsserted(() -> {
                Assertions.assertThat(attemptCount.get()).isEqualTo(2);
                Assertions.assertThat(mockConsumer.committed(topicPartition)).isNull();
                assertTrue(edgeSyncProducer.completeNext());
            });
        await("wait for sync producer to call internal producer a second time")
            .untilAsserted(() -> assertTrue(edgeSyncProducer.completeNext()));

        awaitCommittedOffset(secondOffset + 1);
    }

    private void awaitCommittedOffset(long offset) {
        AtomicReference<OffsetAndMetadata> offsetAndMetadata = new AtomicReference<>();
        await("wait for consumer to commit offset")
            .untilAsserted(
                () -> {
                    mockConsumer.schedulePollTask(() -> {
                        offsetAndMetadata.set(mockConsumer.committed(topicPartition));
                    });
                    Assertions.assertThat(offsetAndMetadata.get()).isNotNull();
                    Assertions.assertThat(offsetAndMetadata.get().offset()).isEqualTo(offset);
                });
    }

    private class TestBatchEventListener implements BatchEventListener<String> {
        private final Queue<List<String>> eventsProcessed = new ConcurrentLinkedQueue<>();
        private final Consumer<List<String>> process;

        TestBatchEventListener() {
            this((events) -> Function.identity());
        }

        TestBatchEventListener(Consumer<List<String>> process) {
            this.process = process;
        }

        @Override
        public void afterSingletonsInstantiated() {
            // do nothing
        }

        @Override
        public void handleEvents(List<String> events, KafkaTopicMetadata topicMetadata, TopicConfig topicConfig) {
            process.accept(events);
            eventsProcessed.add(events);
        }

        public List<List<String>> getBatchesProcessed() {
            return eventsProcessed.stream().collect(Collectors.toList());
        }
    }

    private KafkaEventConsumer<String> constructConsumer(MockConsumer<String, String> consumer,
        BatchEventListener<String> batchEventListener) {
        return constructConsumer(consumer, batchEventListener, PROCESSING_TIMEOUT_MS, BATCH_WAIT_MAX_MS);
    }

    private KafkaEventConsumer<String> constructConsumer(MockConsumer<String, String> consumer,
        BatchEventListener<String> batchEventListener, long processingTimeoutMs, long batchWaitMaxMs) {
        KafkaEventConsumer<String> newConsumer = new KafkaEventConsumer<>(testTopicService,
            TEST_TOPIC,
            consumer,
            "testConsumerGroup",
            "testClientId",
            new JsonDecoder<>(String.class, ObjectMapperProvider.getConfiguredInstance()),
            metricRegistry,
            retryProducer,
            kafkaConsumerRecordProcessor,
            Duration.ZERO,
            processingTimeoutMs,
            MAX_PARTITIONS,
            IN_MEMORY_PARTITION_EVENT_MAX,
            batchWaitMaxMs,
            PERFORMANCE_LOGGER_THRESHOLD_MS,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            "bootstrapServer",
            new AtomicBoolean(true));
        newConsumer.startup(batchEventListener);
        return newConsumer;
    }
}

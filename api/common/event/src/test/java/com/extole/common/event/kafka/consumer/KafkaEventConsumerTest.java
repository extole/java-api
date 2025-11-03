package com.extole.common.event.kafka.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.TopicPartition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.extole.common.event.EventListener;
import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.KafkaTopicMetadata;
import com.extole.common.event.kafka.producer.InternalRetryEventProducer;
import com.extole.common.event.kafka.producer.TestTopicService;
import com.extole.common.event.kafka.serializer.json.JsonDecoder;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.metrics.ExtoleMetricRegistry;

@SpringBootTest
@ComponentScan(basePackages = {
    "com.extole.common.event.kafka.producer",
})
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaEventConsumerTest {
    private static final Topic TEST_TOPIC = new Topic("test", KafkaClusterType.EDGE);
    private static final long TEST_AWAIT_SECONDS = 30;
    private static final long PROCESSING_TIMEOUT_MS = 10000L;
    private static final int MAX_PARTITIONS = 10;
    private static final int IN_MEMORY_PARTITION_EVENT_MAX = 2;
    private static final long BATCH_WAIT_MAX_MS = 100L;
    private static final long PERFORMANCE_LOGGER_THRESHOLD_MS = 1000L;
    private static final int OFFSET_BOUND = 10000000;

    private final AtomicBoolean isShutdownRequired = new AtomicBoolean(false);
    private final AtomicBoolean shutdownClosureDetected = new AtomicBoolean(false);

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
        assertTrue(shutdownClosureDetected.get());
    }

    @Test
    public void testConsumerCommitsOffsetAfterProcessing() {
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(new ConsumerRecord<>(topicPartition.topic(), topicPartition.partition(),
                initialOffset, "test", "\"test1\""));
        });
        TestEventListener listener = new TestEventListener();
        consumer = constructConsumer(mockConsumer, listener);

        await("wait for listener to receive batch of events")

            .untilAsserted(() -> Assertions.assertThat(listener.getEventsProcessed()).isNotEmpty());
        Assertions.assertThat(listener.getEventsProcessed()).size().isEqualTo(1);
        Assertions.assertThat(listener.getEventsProcessed().get(0)).isEqualTo("test1");

        awaitCommittedOffset(initialOffset + 1);
    }

    @Test
    public void testEventListenerInterruptedOnTimeout() {
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(new ConsumerRecord<>(topicPartition.topic(), topicPartition.partition(),
                initialOffset, "test", "\"test1\""));
        });
        AtomicBoolean isInterrupted = new AtomicBoolean(false);
        long processingTimeoutMs = 100L;
        TestEventListener listener = new TestEventListener((event) -> {
            try {
                Thread.sleep(processingTimeoutMs * 10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isInterrupted.set(true);
            }
        });
        consumer = constructConsumer(mockConsumer, listener, processingTimeoutMs);

        await("wait for listener to receive batch of events")

            .untilAsserted(() -> Assertions.assertThat(listener.getEventsProcessed()).isNotEmpty());
        Assertions.assertThat(listener.getEventsProcessed()).size().isEqualTo(1);
        Assertions.assertThat(listener.getEventsProcessed().get(0)).isEqualTo("test1");
        Assertions.assertThat(isInterrupted).isTrue();

        await("wait for sync producer to call internal producer")
            .atLeast(Duration.ofMillis(BATCH_WAIT_MAX_MS))

            .untilAsserted(() -> assertTrue(edgeSyncProducer.completeNext()));
        assertThat(edgeSyncProducer.history().size()).isEqualTo(1);

        awaitCommittedOffset(initialOffset + 1);
    }

    @Test
    public void testConsumerProducesToRetryTopicOnFailure() {
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(new ConsumerRecord<>(topicPartition.topic(), topicPartition.partition(),
                initialOffset, "test", "\"test1\""));
        });
        TestEventListener listener = new TestEventListener((event) -> {
            throw new RuntimeException();
        });
        consumer = constructConsumer(mockConsumer, listener);

        await("wait for sync producer to call internal producer")

            .untilAsserted(() -> assertTrue(edgeSyncProducer.completeNext()));
        assertThat(edgeSyncProducer.history().size()).isEqualTo(1);
        Assertions.assertThat(edgeSyncProducer.history()).allMatch(
            record -> record.topic().equals(testTopicService.getConfig(TEST_TOPIC).getOnFailureTopic().getName()));

        Assertions.assertThat(listener.getEventsProcessed()).isEmpty();

        awaitCommittedOffset(initialOffset + 1);
    }

    @Test
    public void testConsumerDoesNotCommitOffsetOnRetryProductionFailure() {
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(new ConsumerRecord<>(topicPartition.topic(), topicPartition.partition(),
                initialOffset, "test", "\"test1\""));
        });
        AtomicInteger attemptCount = new AtomicInteger(0);
        TestEventListener listener = new TestEventListener((event) -> {
            attemptCount.getAndIncrement();
            throw new RuntimeException();
        });
        consumer = constructConsumer(mockConsumer, listener);
        await("wait for sync producer to call internal producer")

            .untilAsserted(() -> {
                Assertions.assertThat(attemptCount.get()).isEqualTo(1);
                assertTrue(edgeSyncProducer.errorNext(new RuntimeException()));
            });
        Assertions.assertThat(listener.getEventsProcessed()).isEmpty();

        await("wait for sync producer to call internal producer")

            .untilAsserted(() -> {
                Assertions.assertThat(attemptCount.get()).isEqualTo(2);
                Assertions.assertThat(mockConsumer.committed(topicPartition)).isNull();
                assertTrue(edgeSyncProducer.completeNext());
            });

        awaitCommittedOffset(initialOffset + 1);
    }

    @Test
    public void testTopicSubscription() {
        testConsumerCommitsOffsetAfterProcessing();
        TopicPartition secondTopicPartition = addTopicPartition();
        consumer.removeTopic(TEST_TOPIC);

        Set<String> topics = Collections.newSetFromMap(new ConcurrentHashMap<>());
        await("wait for consumer subscription to update")

            .untilAsserted(
                () -> {
                    mockConsumer.schedulePollTask(() -> {
                        topics.addAll(mockConsumer.subscription());
                        topics.removeIf(topic -> !mockConsumer.subscription().contains(topic));
                    });
                    Assertions.assertThat(topics).containsOnly(secondTopicPartition.topic());
                });
    }

    @Test
    public void testSlowPartitionDoesNotBlockOtherPartitionProcessing() {
        String brokenEvent = "broken_event";
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(
                new ConsumerRecord<>(topicPartition.topic(), topicPartition.partition(), initialOffset, "test",
                    "\"" + brokenEvent + "\""));
        });
        TestEventListener listener = new TestEventListener((event) -> {
            if (event.equals(brokenEvent)) {
                throw new RuntimeException();
            }
        });
        consumer = constructConsumer(mockConsumer, listener);
        TopicPartition secondTopicPartition = addTopicPartition();

        await("first partition processor failing")

            .untilAsserted(() -> Assertions.assertThat(edgeSyncProducer.flushed()).isFalse());

        String workingEvent = "rickrolled";
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.addRecord(
                new ConsumerRecord<>(secondTopicPartition.topic(), 0, 1L, "test", "\"" + workingEvent + "\""));
        });

        await("wait for listener to receive batch of events")

            .untilAsserted(() -> Assertions.assertThat(listener.getEventsProcessed()).isNotEmpty());
        Assertions.assertThat(listener.getEventsProcessed()).size().isEqualTo(1);
        Assertions.assertThat(listener.getEventsProcessed().get(0)).isEqualTo(workingEvent);

        AtomicReference<OffsetAndMetadata> firstTopicOffsetAndMetadata = new AtomicReference<>();
        AtomicReference<OffsetAndMetadata> secondTopicOffsetAndMetadata = new AtomicReference<>();
        await("wait for consumer to commit offset for second topic")

            .untilAsserted(
                () -> {
                    mockConsumer.schedulePollTask(() -> {
                        firstTopicOffsetAndMetadata.set(mockConsumer.committed(topicPartition));
                        secondTopicOffsetAndMetadata.set(mockConsumer.committed(secondTopicPartition));
                    });
                    Assertions.assertThat(firstTopicOffsetAndMetadata.get()).isNull();

                    Assertions.assertThat(secondTopicOffsetAndMetadata.get()).isNotNull();
                    Assertions.assertThat(secondTopicOffsetAndMetadata.get().offset()).isEqualTo(2L);
                });

        edgeSyncProducer.flush();
    }

    @Test
    public void testPartitionPausedAfterInMemoryMaxHit() {
        int eventsToProduce = IN_MEMORY_PARTITION_EVENT_MAX + 2;
        mockConsumer.schedulePollTask(() -> {
            for (int i = 0; i < eventsToProduce; i++) {
                mockConsumer.addRecord(new ConsumerRecord<>(topicPartition.topic(), topicPartition.partition(),
                    initialOffset + i, "test", "\"test" + i + "\""));
            }
        });
        AtomicBoolean shouldFail = new AtomicBoolean(true);
        TestEventListener listener = new TestEventListener((event) -> {
            if (shouldFail.get()) {
                throw new RuntimeException();
            }
        });
        consumer = constructConsumer(mockConsumer, listener);

        Set<TopicPartition> pausedTopicPartitions = Collections.newSetFromMap(new ConcurrentHashMap<>());
        await("wait for consumer to pause partition")

            .untilAsserted(() -> {
                mockConsumer.schedulePollTask(() -> {
                    pausedTopicPartitions.addAll(mockConsumer.paused());
                    pausedTopicPartitions.removeIf(topicPartition -> !mockConsumer.paused().contains(topicPartition));
                });
                Assertions.assertThat(pausedTopicPartitions).isNotEmpty();
            });
        shouldFail.set(false);

        AtomicReference<OffsetAndMetadata> offsetAndMetadata = new AtomicReference<>();
        await("wait for consumer to commit offset and unpause partition")

            .untilAsserted(
                () -> {
                    edgeSyncProducer.errorNext(new RuntimeException());
                    mockConsumer.schedulePollTask(() -> {
                        offsetAndMetadata.set(mockConsumer.committed(topicPartition));
                        pausedTopicPartitions.addAll(mockConsumer.paused());
                        pausedTopicPartitions
                            .removeIf(topicPartition -> !mockConsumer.paused().contains(topicPartition));
                    });
                    Assertions.assertThat(listener.getEventsProcessed().size()).isEqualTo(eventsToProduce);
                    Assertions.assertThat(offsetAndMetadata.get()).isNotNull();
                    Assertions.assertThat(offsetAndMetadata.get().offset()).isEqualTo(initialOffset + eventsToProduce);
                    Assertions.assertThat(pausedTopicPartitions).isEmpty();
                });
    }

    @Test
    public void testShutdownRequiredClosureShutsDownConsumer() {
        TestEventListener listener = new TestEventListener();
        consumer = constructConsumer(mockConsumer, listener);
        isShutdownRequired.set(true);

        await("wait for consumer to shutdown")

            .untilAsserted(
                () -> {
                    assertTrue(mockConsumer.closed());
                });
    }

    private void awaitCommittedOffset(long offset) {
        AtomicReference<OffsetAndMetadata> offsetAndMetadata = new AtomicReference<>();
        await("wait for consumer to commit offset " + offset + " for subscription " + mockConsumer.subscription()
            + " including topicPartition " + topicPartition)

                .untilAsserted(
                    () -> {
                        mockConsumer.schedulePollTask(() -> {
                            offsetAndMetadata.set(mockConsumer.committed(topicPartition));
                        });
                        Assertions.assertThat(offsetAndMetadata.get()).isNotNull();
                        Assertions.assertThat(offsetAndMetadata.get().offset()).isEqualTo(offset);
                    });
    }

    private static class TestEventListener implements EventListener<String> {
        private final Queue<String> eventsProcessed = new ConcurrentLinkedQueue<>();
        private final Consumer<String> process;

        TestEventListener() {
            this((event) -> Function.identity());
        }

        TestEventListener(Consumer<String> process) {
            this.process = process;
        }

        @Override
        public void afterSingletonsInstantiated() {
            // do nothing
        }

        @Override
        public void handleEvent(String event, KafkaTopicMetadata topicMetadata) {
            process.accept(event);
            eventsProcessed.add(event);
        }

        public List<String> getEventsProcessed() {
            return new ArrayList<>(eventsProcessed);
        }
    }

    private TopicPartition addTopicPartition() {
        String secondTopicName = "second_test_topic";

        TopicPartition secondTopicPartition = new TopicPartition(secondTopicName, 0);
        HashMap<TopicPartition, Long> endOffsets = new HashMap<>();
        endOffsets.put(secondTopicPartition, Long.valueOf(0L));
        mockConsumer.updateEndOffsets(endOffsets);
        consumer.addTopic(new Topic(secondTopicName, KafkaClusterType.EDGE));
        Set<TopicPartition> topicPartitions = new HashSet<>(Arrays.asList(topicPartition, secondTopicPartition));

        Set<String> subscribedTopics = Collections.newSetFromMap(new ConcurrentHashMap<>());
        await("wait for consumer subscription to update subscriptions " + subscribedTopics)

            .untilAsserted(
                () -> {
                    mockConsumer.schedulePollTask(() -> {
                        subscribedTopics.addAll(mockConsumer.subscription());
                        subscribedTopics.removeIf(topic -> !mockConsumer.subscription().contains(topic));
                    });
                    Assertions.assertThat(subscribedTopics)
                        .containsAll(Arrays.asList(TEST_TOPIC.getName(), secondTopicName));
                    Assertions.assertThat(mockConsumer.assignment()).containsOnly(topicPartition);
                });

        mockConsumer.schedulePollTask(() -> {
            mockConsumer.rebalance(topicPartitions);
        });

        Set<TopicPartition> assignment = Collections.newSetFromMap(new ConcurrentHashMap<>());
        await("wait for consumer assignment to update")

            .untilAsserted(
                () -> {
                    mockConsumer.schedulePollTask(() -> {
                        assignment.addAll(mockConsumer.assignment());
                        assignment.removeIf(topicPartition -> !mockConsumer.assignment().contains(topicPartition));
                    });
                    Assertions.assertThat(assignment).isEqualTo(topicPartitions);
                });
        return secondTopicPartition;
    }

    private KafkaEventConsumer<String> constructConsumer(MockConsumer<String, String> consumer,
        TestEventListener listener) {
        return constructConsumer(consumer, listener, PROCESSING_TIMEOUT_MS);
    }

    private KafkaEventConsumer<String> constructConsumer(MockConsumer<String, String> consumer,
        TestEventListener listener, long processingTimeoutMs) {
        KafkaEventConsumer<String> newConsumer = new KafkaEventConsumer<>(testTopicService,
            TEST_TOPIC,
            consumer,
            "testConsumerGroup",
            "testClientId",
            new JsonDecoder<>(String.class, ObjectMapperProvider.getConfiguredInstance()),
            metricRegistry,
            retryProducer, kafkaConsumerRecordProcessor,
            Duration.ZERO,
            processingTimeoutMs,
            MAX_PARTITIONS,
            IN_MEMORY_PARTITION_EVENT_MAX,
            BATCH_WAIT_MAX_MS,
            PERFORMANCE_LOGGER_THRESHOLD_MS,
            Optional.empty(),
            Optional.of((context) -> Boolean.valueOf(isShutdownRequired.get())),
            Optional.of((context) -> {
                shutdownClosureDetected.set(context.isShutdown());
            }),
            "bootstrapServer",
            new AtomicBoolean(true));
        newConsumer.startup(listener);
        return newConsumer;
    }
}

package com.extole.common.event.kafka.consumer;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RebalanceInProgressException;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.event.BatchEventListener;
import com.extole.common.event.EventListener;
import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.KafkaExpectedRetryException;
import com.extole.common.event.KafkaRetryException;
import com.extole.common.event.KafkaWaitException;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.ConsumerShutdownContext;
import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.kafka.ExtoleKafkaConsumer;
import com.extole.common.event.kafka.KafkaEventConsumerContext;
import com.extole.common.event.kafka.KafkaEventConsumerRuntimeException;
import com.extole.common.event.kafka.KafkaTopicMetadata;
import com.extole.common.event.kafka.PartitionOffsets;
import com.extole.common.event.kafka.TaskCancellingException;
import com.extole.common.event.kafka.TraceableProcessingTask;
import com.extole.common.event.kafka.producer.InternalRetryEventProducer;
import com.extole.common.event.kafka.serializer.json.JsonDecoder;
import com.extole.common.event.topic.TopicConfig;
import com.extole.common.event.topic.TopicService;
import com.extole.common.lang.ExtoleThreadFactory;
import com.extole.common.log.PerformanceLogger;
import com.extole.common.metrics.ExtoleHistogram;
import com.extole.common.metrics.ExtoleMetricRegistry;

public class KafkaEventConsumer<T> implements ExtoleKafkaConsumer<T> {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventConsumer.class);
    private static final long RETRY_BACKOFF_MS = 500L;
    private static final String HISTOGRAM_POSTFIX = ".duration";
    private static final String PROCESSING_DURATION_HISTOGRAM_POSTFIX = ".processing.duration";
    private static final String BATCH_SIZE_POSTFIX = ".batch.size";
    private static final String PARTITIONS_COMMITTED_POSTFIX = ".partitions.committed";
    private static final long METRICS_REPORTING_GRACE_PERIOD_MS = 30_000L;
    static final String METRIC_PREFIX = "com.extole.common.event.kafka.KafkaEventConsumer";

    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final TopicService topicService;
    private final ExecutorService consumerExecutor;
    private final ThreadPoolExecutor partitionProcessingExecutor;
    private final InternalRetryEventProducer retryProducer;
    private final String groupId;
    private final String clientId;
    private final JsonDecoder<T> decoder;
    private final ExtoleMetricRegistry metricRegistry;
    private final Consumer<String, String> consumer;
    private final Duration maxPollBlockWait;
    private final long processingTimeoutMs;
    private final int inMemoryPartitionEventMax;
    private final long batchWaitMaxMs;
    private final Optional<Long> partitionLookbackPeriodMs;
    private final Optional<Function<KafkaEventConsumerContext, Boolean>> isShutdownRequiredClosure;
    private final Optional<java.util.function.Consumer<ConsumerShutdownContext>> onShutdownCallback;
    private final Set<String> topicsToSubscribeTo = Sets.newConcurrentHashSet();
    private final Set<String> topicsToUnsubscribeFrom = Sets.newConcurrentHashSet();
    private final Set<String> subscription = Sets.newConcurrentHashSet();
    private final Map<TopicPartition, Long> partitionOffsetsBeforeLookback = Maps.newConcurrentMap();
    private final KafkaClusterType kafkaClusterType;
    private final String bootstrapServer;
    private final long performanceLoggerLogThresholdMs;
    private final KafkaConsumerRecordProcessor kafkaConsumerRecordProcessor;

    KafkaEventConsumer(TopicService topicService,
        Topic topic,
        Consumer<String, String> consumer,
        String groupId,
        String clientId,
        JsonDecoder<T> decoder,
        ExtoleMetricRegistry metricRegistry,
        InternalRetryEventProducer retryProducer,
        KafkaConsumerRecordProcessor kafkaConsumerRecordProcessor,
        Duration maxPollBlockWait,
        long processingTimeoutMs,
        int maxPartitions,
        int inMemoryPartitionEventMax,
        long batchWaitMaxMs,
        long performanceLoggerLogThresholdMs,
        Optional<Long> partitionLookbackPeriodMs,
        Optional<Function<KafkaEventConsumerContext, Boolean>> isShutdownRequiredClosure,
        Optional<java.util.function.Consumer<ConsumerShutdownContext>> onShutdownCallback,
        String bootstrapServer,
        AtomicBoolean consumerFactoryHealth) {
        this.topicService = topicService;
        this.retryProducer = retryProducer;
        this.kafkaConsumerRecordProcessor = kafkaConsumerRecordProcessor;
        this.consumer = consumer;
        this.decoder = decoder;
        this.metricRegistry = metricRegistry;
        this.maxPollBlockWait = maxPollBlockWait;
        this.processingTimeoutMs = processingTimeoutMs;
        this.partitionProcessingExecutor = new ThreadPoolExecutor(1, maxPartitions,
            maxPollBlockWait.multipliedBy(2L).toMillis(), TimeUnit.MILLISECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.AbortPolicy());
        this.groupId = groupId;
        this.clientId = clientId;
        this.consumerExecutor = Executors
            .newSingleThreadExecutor(
                new ExtoleThreadFactory(KafkaEventConsumer.class.getSimpleName() + "-" + groupId, (t, e) -> {
                    consumerFactoryHealth.set(false);
                    LOG.error("#FATAL Uncaught Exception from thread {} - marking consumer factory unhealthy", t, e);
                }));
        this.inMemoryPartitionEventMax = inMemoryPartitionEventMax;
        this.batchWaitMaxMs = batchWaitMaxMs;
        this.partitionLookbackPeriodMs = partitionLookbackPeriodMs;
        this.isShutdownRequiredClosure = isShutdownRequiredClosure;
        this.onShutdownCallback = onShutdownCallback;
        this.kafkaClusterType = topic.getClusterType();
        this.bootstrapServer = bootstrapServer;
        this.performanceLoggerLogThresholdMs = performanceLoggerLogThresholdMs;
        addTopic(topic);
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public void addTopic(Topic topic) {
        if (kafkaClusterType != topic.getClusterType()) {
            throw new KafkaEventConsumerRuntimeException("kafka consumer of cluster " + kafkaClusterType
                + " cannot also consumer from cluster " + topic.getClusterType());
        }
        if (subscription.contains(topic.getName())) {
            LOG.trace("Ignoring topic {} addition - already subscribed", topic.getName());
            return;
        }
        TopicConfig topicConfig = topicService.getConfig(topic);
        while (!topicConfig.getTopic().getName().endsWith(TopicService.DEAD_SUFFIX)) {
            try {
                topicService.validateTopic(topicConfig.getTopic(), bootstrapServer);
            } catch (ExecutionException e) {
                LOG.error("Failed to validate topic {}", topic.getName(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new KafkaEventConsumerRuntimeException("Interrupted while validating the topic: " + topic);
            }
            LOG.info("Consumer adding config {} to subscribe to", topicConfig);
            topicsToUnsubscribeFrom.remove(topicConfig.getTopic().getName());
            topicsToSubscribeTo.add(topicConfig.getTopic().getName());
            topicConfig = topicService.getConfig(topicConfig.getOnFailureTopic());
        }
    }

    @Override
    public void removeTopic(Topic topic) {
        topicsToSubscribeTo.remove(topic.getName());
        topicsToUnsubscribeFrom.add(topic.getName());
    }

    @Override
    public void startup(EventListener<T> eventListener) {
        startup(null, eventListener);
    }

    @Override
    public void startup(BatchEventListener<T> batchEventListener) {
        startup(batchEventListener, null);
    }

    @Override
    public void shutdown() {
        LOG.warn("{} Kafka Consumer shutting down", clientId);
        if (!shutdown.getAndSet(true)) {
            // interrupt long poll of consumer to speed up shutdown
            consumer.wakeup();
        }

        consumerExecutor.shutdown();
        partitionProcessingExecutor.shutdown();

        long executorShutdownWaitTimeoutMs = processingTimeoutMs + Duration.ofSeconds(1).toMillis();
        try {
            boolean processingComplete =
                partitionProcessingExecutor.awaitTermination(executorShutdownWaitTimeoutMs, TimeUnit.MILLISECONDS);
            if (!processingComplete) {
                LOG.error("{} Failed to wait for processingExecutor to shutdown after waiting {}ms",
                    clientId, String.valueOf(executorShutdownWaitTimeoutMs));
            }
            boolean isConsumerShutdown =
                consumerExecutor.awaitTermination(executorShutdownWaitTimeoutMs, TimeUnit.MILLISECONDS);
            if (!isConsumerShutdown) {
                LOG.error("{} Failed to wait for the consumer to shutdown after waiting {}ms",
                    clientId, String.valueOf(executorShutdownWaitTimeoutMs));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Shutdown thread for the {} Kafka Consumer interrupted while waiting for the consumers "
                + "to terminate", clientId, e);
        }
        LOG.warn("{} Kafka Consumer has been shutdown.", clientId);
    }

    public boolean isShutdown() {
        return shutdown.get();
    }

    private void startup(BatchEventListener<T> batchEventListener, EventListener<T> eventListener) {
        if (shutdown.get()) {
            throw new KafkaEventConsumerRuntimeException(
                "Kafka Consumers are shutting down! Cannot start another event listener for Kafka consumer: "
                    + clientId);
        }
        LOG.warn("Starting up the {} Kafka Consumer", clientId);
        consumerExecutor.execute(new ConsumerTask(batchEventListener, eventListener));
    }

    private final class ConsumerTask implements Runnable {
        private final ConcurrentMap<String, ExtoleHistogram> topicDurationHistogram = new ConcurrentHashMap<>();
        private final ConcurrentMap<String, ExtoleHistogram> processingDurationHistogram = new ConcurrentHashMap<>();
        private final ExtoleHistogram batchSizeHistogram;
        private final ExtoleHistogram partitionsCommittedHistogram;
        private final ExtoleRebalanceListener extoleRebalanceListener;
        private final Optional<BatchEventListener<T>> batchEventListener;
        private final Optional<EventListener<T>> eventListener;
        private final Map<TopicPartition, PartitionContext> partitionContexts = new HashMap<>();

        private ConsumerTask(BatchEventListener<T> batchEventListener, EventListener<T> eventListener) {
            this.batchSizeHistogram =
                metricRegistry
                    .histogram(METRIC_PREFIX + "." + groupId + BATCH_SIZE_POSTFIX);
            this.partitionsCommittedHistogram = metricRegistry
                .histogram(METRIC_PREFIX + "." + groupId + PARTITIONS_COMMITTED_POSTFIX);
            this.extoleRebalanceListener =
                new ExtoleRebalanceListener(consumer, groupId, clientId, metricRegistry, partitionContexts,
                    processingTimeoutMs, partitionLookbackPeriodMs, kafkaClusterType, bootstrapServer,
                    partitionOffsetsBeforeLookback);
            this.batchEventListener = Optional.ofNullable(batchEventListener);
            this.eventListener = Optional.ofNullable(eventListener);
        }

        @Override
        public void run() {
            // Kafka Consumer can only be interacted with using a single thread - thus a master task responsible for
            // both polling and committing
            while (!shutdown.get()) {
                if (!topicsToSubscribeTo.isEmpty() || !topicsToUnsubscribeFrom.isEmpty()) {
                    adjustSubscription();
                }
                if (consumer.subscription().isEmpty()) {
                    try {
                        LOG.debug("Consumer {} has no topics. Waiting {}ms", clientId, Long.valueOf(RETRY_BACKOFF_MS));
                        Thread.sleep(RETRY_BACKOFF_MS);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                        LOG.warn("Failed to backoff for {}ms while retrying startup of kafka consumer {}",
                            Long.valueOf(RETRY_BACKOFF_MS), clientId, e1);
                    }
                    continue;
                }
                try {
                    Set<TopicPartition> pausedPartitions = consumer.paused();
                    for (PartitionContext partitionContext : partitionContexts.values()) {
                        if ((shutdown.get() || partitionContext.getRecordCount() > inMemoryPartitionEventMax)) {
                            if (!pausedPartitions.contains(partitionContext.getTopicPartition())) {
                                LOG.debug("Pausing partition {} with {} recordsToProcess",
                                    partitionContext.getTopicPartition(),
                                    String.valueOf(partitionContext.getRecordCount()));
                                consumer.pause(Collections.singleton(partitionContext.getTopicPartition()));
                            }
                        } else if (pausedPartitions.contains(partitionContext.getTopicPartition())) {
                            LOG.debug("Resuming partition {} with {} recordsToProcess",
                                partitionContext.getTopicPartition(),
                                String.valueOf(partitionContext.getRecordCount()));
                            consumer.resume(Collections.singleton(partitionContext.getTopicPartition()));
                        }
                    }

                    ConsumerRecords<String, String> consumerRecords = consumer.poll(maxPollBlockWait);
                    batchSizeHistogram.update(consumerRecords.count());

                    if (isShutdownRequiredClosure.isPresent()) {
                        Map<TopicPartition, PartitionOffsets> partitionOffsets = getPartitionOffsets();
                        KafkaEventConsumerContext eventConsumerContext =
                            new KafkaEventConsumerContext(partitionOffsets);
                        Boolean shutdownRequired = isShutdownRequiredClosure.get().apply(eventConsumerContext);
                        if (Boolean.TRUE.equals(shutdownRequired)) {
                            LOG.info("Shutdown for consumer={}. Records size={}, " +
                                "partitionOffsets={}", clientId, Integer.valueOf(consumerRecords.count()),
                                partitionOffsets);
                            shutdown.set(true);
                        }
                    }

                    LOG.trace("Kafka consumer {} polled {} partitions", clientId,
                        String.valueOf(consumerRecords.partitions()));
                    for (TopicPartition partition : consumerRecords.partitions()) {
                        List<ConsumerRecord<String, String>> partitionRecords = consumerRecords.records(partition);
                        if (shutdown.get()) {
                            break;
                        }
                        PartitionContext partitionContext = partitionContexts.get(partition);
                        if (partitionContext == null) {
                            PartitionContext newPartitionContext =
                                new PartitionContext(partition, partitionRecords.get(0).offset());
                            LOG.info("initialized new partitionContext {}", newPartitionContext);
                            partitionProcessingExecutor.execute(() -> {
                                if (eventListener.isPresent()) {
                                    processPartition(newPartitionContext, eventListener.get());
                                } else {
                                    batchProcessPartition(newPartitionContext, batchEventListener.get());
                                }
                            });
                            partitionContexts.put(partition, newPartitionContext);
                            partitionContext = newPartitionContext;
                        }
                        partitionContext.addRecords(partitionRecords);
                    }
                    Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
                    for (PartitionContext context : partitionContexts.values()) {
                        Optional<OffsetAndMetadata> offset = context.getOffsetToCommit();
                        if (offset.isPresent()) {
                            offsetsToCommit.put(context.getTopicPartition(), offset.get());
                        }
                    }
                    partitionsCommittedHistogram.update(offsetsToCommit.size());
                    try {
                        LOG.trace("committing offsets {}", offsetsToCommit);
                        consumer.commitSync(offsetsToCommit);
                        for (TopicPartition topicPartition : offsetsToCommit.keySet()) {
                            partitionContexts.get(topicPartition)
                                .setLastOffsetCommitted(offsetsToCommit.get(topicPartition).offset());
                        }
                    } catch (RebalanceInProgressException e) {
                        LOG.info("{} delaying commit for partitions {} due to rebalance", clientId,
                            partitionContexts.keySet());
                    } catch (CommitFailedException e) {
                        LOG.error("{} failed to commit partitions {}", clientId, partitionContexts.keySet(), e);
                    }
                } catch (WakeupException e) {
                    LOG.info("{} interrupted by wakeup call", clientId);
                } catch (Exception e) {
                    LOG.error("{} processing failed holding partitions {}", clientId, partitionContexts.keySet(),
                        e);
                    try {
                        Thread.sleep(RETRY_BACKOFF_MS);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                        LOG.error("Failed to backoff retrying consumer processing", e1);
                    }
                }
            }
            LOG.debug("Consumer {} task completed. unsubscribe {}", clientId, Boolean.valueOf(shutdown.get()));
            consumer.unsubscribe();
            LOG.debug("Consumer {} unsubscribed. shutdown {}", clientId, Boolean.valueOf(shutdown.get()));
            consumer.close(Duration.ofMillis(processingTimeoutMs));
            onShutdownCallback
                .ifPresent(function -> function.accept(
                    new ConsumerShutdownContext(shutdown.get(), groupId)));
        }

        private void adjustSubscription() {
            LOG.info("Consumer {} adjusting subscription. {} to add, {} to remove", clientId,
                topicsToSubscribeTo, topicsToUnsubscribeFrom);
            Set<String> subscriptions = new HashSet<>(consumer.subscription());
            subscriptions.addAll(topicsToSubscribeTo);
            subscriptions.removeAll(topicsToUnsubscribeFrom);
            consumer.subscribe(subscriptions, extoleRebalanceListener);
            topicsToSubscribeTo.removeAll(subscriptions);
            topicsToUnsubscribeFrom.removeIf(topic -> !subscriptions.contains(topic));
            subscription.clear();
            subscription.addAll(subscriptions);
        }

        private void updateTopicDuration(String topic, long value) {
            topicDurationHistogram.computeIfAbsent(topic, key -> metricRegistry
                .histogram(METRIC_PREFIX + "." + topic + HISTOGRAM_POSTFIX)).update(value);
        }

        private ExtoleHistogram getProcessingDurationHistogram(String topic) {
            return processingDurationHistogram.computeIfAbsent(topic, key -> metricRegistry
                .histogram(METRIC_PREFIX + "." + topic + PROCESSING_DURATION_HISTOGRAM_POSTFIX));
        }

        private Map<TopicPartition, PartitionOffsets> getPartitionOffsets() {
            Set<TopicPartition> partitions = new HashSet<>();
            for (String topic : consumer.subscription()) {
                List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
                if (partitionInfos != null) {
                    for (PartitionInfo partitionInfo : partitionInfos) {
                        partitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
                    }
                }
            }
            Map<TopicPartition, Long> topicEndOffsets = consumer.endOffsets(partitions);
            Map<TopicPartition, OffsetAndMetadata> committedOffsets = consumer.committed(partitions, maxPollBlockWait);

            Map<TopicPartition, PartitionOffsets> partitionOffsets = partitions
                .stream()
                .collect(Collectors.toMap(partition -> partition,
                    partition -> {
                        OffsetAndMetadata partitionOffsetAndMetadata = committedOffsets.get(partition);
                        long committedOffset =
                            partitionOffsetAndMetadata != null ? partitionOffsetAndMetadata.offset() : 0;
                        return new PartitionOffsets(topicEndOffsets.get(partition), Long.valueOf(committedOffset));
                    }));
            return partitionOffsets;
        }

        private void processPartition(PartitionContext partitionContext, EventListener<T> listener) {
            Thread.currentThread().setName(partitionContext.getTopicPartition().toString() + "-partition-processor");
            ExecutorService eventProcessingExecutor = Executors.newSingleThreadExecutor(
                new ExtoleThreadFactory(partitionContext.getTopicPartition().toString() + "-event-processor"));
            TopicConfig topicConfig = getConfig(partitionContext);
            Topic onFailureTopic = topicConfig.getOnFailureTopic();
            ConsumerRecord<String, String> record = null;
            Instant partitionProcessingStartTime = Instant.now();
            while (canProcess(partitionContext)) {
                if (record == null) {
                    try {
                        record = partitionContext.getNextRecord().orElse(null);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LOG.warn("Got interrupted while trying to get record for topic={} are we shutting down?={}",
                            partitionContext.getTopicPartition(), Boolean.valueOf(shutdown.get()));
                    } catch (Exception e) {
                        LOG.error("{} failed to get next record", partitionContext.getTopicPartition(), e);
                    }
                }
                if (record == null) {
                    continue;
                }

                Instant startProcessingTime = Instant.now();
                long offset = record.offset();
                try {
                    updateDurationIfNecessary(topicConfig, record, startProcessingTime, partitionProcessingStartTime);
                    ConsumerRecord<String, String> finalRecord = record;
                    PerformanceLogger performanceLogger = new PerformanceLogger();
                    TraceableProcessingTask task = new TraceableProcessingTask(() -> {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("{} processing offset {}", partitionContext.getTopicPartition(),
                                Long.valueOf(offset));
                        }
                        kafkaConsumerRecordProcessor.handleEvent(listener, decoder, finalRecord,
                            new KafkaTopicMetadata(topicConfig.getAttempt(), topicConfig.getTopic(),
                                partitionContext.getTopicPartition().partition(), offset,
                                topicConfig.getOnFailureTopic(), performanceLogger));
                        return null;
                    });
                    Future<Void> processEventTask = eventProcessingExecutor.submit(task);
                    try {
                        processEventTask.get(processingTimeoutMs, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException e) {
                        processEventTask.cancel(true);
                        if (!task.isSuccess()) {
                            addTaskStackTrace(task, e);
                            List<String> performanceLogs = performanceLogger
                                .getMessagesWithDurationAbove(performanceLoggerLogThresholdMs);
                            LOG.error("{} failed to process event after {}ms. Sending to retry topic {}, " +
                                "performanceLogs {}. Event: {}", partitionContext.getTopicPartition(),
                                Long.valueOf(Duration.between(startProcessingTime, Instant.now()).toMillis()),
                                onFailureTopic, performanceLogs, record.value(), e);
                            produceToRetryTopic(topicConfig, record, e);
                        } else {
                            LOG.warn("{} - event processing completed despite timeout",
                                partitionContext.getTopicPartition());
                        }
                    } catch (ExecutionException | RuntimeException e) {
                        if (e.getCause() != null
                            && KafkaWaitException.class
                                .isAssignableFrom(e.getCause().getClass())) {
                            KafkaWaitException cause = (KafkaWaitException) e.getCause();
                            LOG.info("{} sleeping for {}ms after receiving backoff exception: {}",
                                partitionContext.getTopicPartition(), cause.getWaitMs(), cause.getMessage());
                            try {
                                Thread.sleep(cause.getWaitMs());
                            } catch (InterruptedException e1) {
                                Thread.currentThread().interrupt();
                                LOG.error("{} failed to backoff for {}ms ", partitionContext.getTopicPartition(),
                                    cause.getWaitMs(), e1);
                            }
                            continue;
                        }
                        Throwable cause;
                        if (e.getCause() != null
                            && KafkaRetryException.class.isAssignableFrom(e.getCause().getClass())) {
                            cause = e.getCause();
                            LOG.info("{} retrying event {} after {}ms. sending to retry topic {}",
                                partitionContext.getTopicPartition(), record.value(),
                                Duration.between(startProcessingTime, Instant.now()).toMillis(), onFailureTopic, cause);
                        } else {
                            cause =
                                ((e instanceof ExecutionException) && e.getCause() != null) ? e.getCause() : e;
                            LOG.error("{} failed to process event after {}ms. Sending to retry topic {}. Event: {}",
                                partitionContext.getTopicPartition(),
                                Long.valueOf(Duration.between(startProcessingTime, Instant.now()).toMillis()),
                                onFailureTopic, record.value(), cause);
                        }
                        if (!task.isSuccess()) {
                            produceToRetryTopic(topicConfig, record, cause);
                        }
                    }
                    record = null;
                    getProcessingDurationHistogram(topicConfig.getTopic().getName()).update(startProcessingTime,
                        Instant.now());
                    partitionContext.setLastProcessedOffset(offset);
                } catch (Exception e) {
                    Throwable cause = ((e instanceof ExecutionException) && e.getCause() != null) ? e.getCause() : e;
                    LOG.error("{} failed to process record {}", partitionContext.getTopicPartition(), record, cause);
                }
            }
            eventProcessingExecutor.shutdown();
            partitionContext.markAsDone();
        }

        private void updateDurationIfNecessary(TopicConfig topicConfig, ConsumerRecord<String, String> record,
            Instant startProcessingTime, Instant partitionProcessingStartTime) {
            if (partitionLookbackPeriodMs.isEmpty()
                || Instant.ofEpochMilli(record.timestamp()).isAfter(partitionProcessingStartTime)
                || isTimeToReportMetrics(partitionProcessingStartTime)) {
                updateTopicDuration(topicConfig.getTopic().getName(),
                    startProcessingTime.toEpochMilli() - record.timestamp());
            }
        }

        private boolean isTimeToReportMetrics(Instant partitionProcessingStartTime) {
            return Instant.now().minus(METRICS_REPORTING_GRACE_PERIOD_MS, ChronoUnit.MILLIS)
                .isAfter(partitionProcessingStartTime);
        }

        private void batchProcessPartition(PartitionContext partitionContext, BatchEventListener<T> listener) {
            Thread.currentThread().setName(partitionContext.getTopicPartition().toString() + "-partition-processor");
            LinkedList<ConsumerRecord<String, String>> records = new LinkedList<>();
            TopicConfig topicConfig = getConfig(partitionContext);
            ExecutorService eventProcessingExecutor = Executors.newSingleThreadExecutor(
                new ExtoleThreadFactory(partitionContext.getTopicPartition().toString() + "-batch-event-processor"));
            while (canProcess(partitionContext)) {
                try {
                    Instant batchWaitEnd = Instant.now().plusMillis(batchWaitMaxMs);
                    while (canProcess(partitionContext) && records.size() < topicConfig.getBatchSize()) {
                        Optional<ConsumerRecord<String, String>> record = partitionContext.getNextRecord();
                        if (record.isPresent()) {
                            updateTopicDuration(partitionContext.getTopicPartition().topic(),
                                Instant.now().toEpochMilli() - record.get().timestamp());
                            records.add(record.get());
                        }
                        if (Instant.now().isAfter(batchWaitEnd)) {
                            break;
                        }
                    }
                    if (canProcess(partitionContext) && !records.isEmpty()) {
                        Instant startProcessingTime = Instant.now();
                        try {
                            PerformanceLogger performanceLogger = new PerformanceLogger();
                            TraceableProcessingTask task = new TraceableProcessingTask(() -> {
                                kafkaConsumerRecordProcessor.handleEvents(records, decoder, listener,
                                    new KafkaTopicMetadata(topicConfig.getAttempt(), topicConfig.getTopic(),
                                        partitionContext.getTopicPartition().partition(), records.getFirst().offset(),
                                        topicConfig.getOnFailureTopic(), performanceLogger),
                                    topicConfig);
                                return null;
                            });
                            Future<Void> processEventsTask = eventProcessingExecutor.submit(task);
                            try {
                                processEventsTask.get(processingTimeoutMs, TimeUnit.MILLISECONDS);
                            } catch (TimeoutException e) {
                                processEventsTask.cancel(true);
                                if (!task.isSuccess()) {
                                    addTaskStackTrace(task, e);
                                    List<String> performanceLogs = performanceLogger
                                        .getMessagesWithDurationAbove(performanceLoggerLogThresholdMs);
                                    LOG.error("{} failed to process {} events after {}ms. sending to retry topic {}" +
                                        "performanceLogs {}",
                                        partitionContext.getTopicPartition(), String.valueOf(records.size()),
                                        String.valueOf(Duration.between(startProcessingTime, Instant.now()).toMillis()),
                                        topicConfig.getOnFailureTopic(), performanceLogs, e);
                                    produceToRetryTopic(topicConfig, records, partitionContext, e);
                                } else {
                                    LOG.warn("{} - {} events processing completed despite timeout",
                                        partitionContext.getTopicPartition(), String.valueOf(records.size()));
                                }
                            }
                            if (!records.isEmpty()) {
                                getProcessingDurationHistogram(partitionContext.getTopicPartition().topic())
                                    .update(startProcessingTime, Instant.now());
                                partitionContext.setLastProcessedOffset(records.getLast().offset());
                                records.clear();
                            }
                        } catch (Exception e) {
                            Throwable cause;
                            if (e.getCause() != null
                                && KafkaRetryException.class.isAssignableFrom(e.getCause().getClass())) {
                                cause = e.getCause();
                                LOG.info("{} failed to process {} events after {}ms. sending to retry topic {}",
                                    partitionContext.getTopicPartition(), String.valueOf(records.size()),
                                    Duration.between(startProcessingTime, Instant.now()).toMillis(),
                                    topicConfig.getOnFailureTopic(), cause);
                            } else {
                                cause =
                                    ((e instanceof ExecutionException) && e.getCause() != null) ? e.getCause() : e;
                                LOG.error("{} failed to process {} events after {}ms. sending to retry topic {}",
                                    partitionContext.getTopicPartition(), String.valueOf(records.size()),
                                    Duration.between(startProcessingTime, Instant.now()).toMillis(),
                                    topicConfig.getOnFailureTopic(), cause);
                            }
                            produceToRetryTopic(topicConfig, records, partitionContext, cause);
                        }
                    }
                } catch (Exception e) {
                    Throwable cause = ((e instanceof ExecutionException) && e.getCause() != null) ? e.getCause() : e;
                    LOG.error("{} failed to process record batch {}", partitionContext.getTopicPartition(),
                        String.valueOf(records.size()), cause);
                }
            }
            eventProcessingExecutor.shutdown();
            partitionContext.markAsDone();
        }

        private boolean canProcess(PartitionContext partitionContext) {
            return !(shutdown.get() || partitionContext.isRevoked());
        }

        private void addTaskStackTrace(TraceableProcessingTask task, TimeoutException e) {
            StackTraceElement[] taskStackTrace = task.getStackTrace();
            if (taskStackTrace.length > 0) {
                Exception cancellingException =
                    new TaskCancellingException("Capturing stack trace of task as we cancel");
                cancellingException.setStackTrace(task.getStackTrace());
                e.addSuppressed(cancellingException);
            }
        }

        private void produceToRetryTopic(TopicConfig topicConfig, LinkedList<ConsumerRecord<String, String>> records,
            PartitionContext partitionContext, Throwable cause)
            throws InterruptedException, ExecutionException, EventTooLargeException {
            while (!records.isEmpty()) {
                ConsumerRecord<String, String> record = records.peek();
                produceToRetryTopic(topicConfig, record, cause);
                partitionContext.setLastProcessedOffset(record.offset());
                records.removeFirst();
            }
        }

        private void produceToRetryTopic(TopicConfig topicConfig, ConsumerRecord<String, String> record,
            Throwable cause)
            throws InterruptedException, ExecutionException, EventTooLargeException {
            if (topicConfig.getOnFailureTopic().getName().endsWith(TopicService.DEAD_SUFFIX)) {
                String logFormat = "Kafka consumer {} sending a failed record to dead - {}";
                if (KafkaExpectedRetryException.class.isAssignableFrom(cause.getClass())) {
                    LOG.info(logFormat, groupId, record, cause);
                } else {
                    LOG.error(logFormat, groupId, record, cause);
                }
            } else {
                LOG.trace("Kafka consumer {} sending a failed record to retry - {}", groupId, record, cause);
            }
            retryProducer.sendRecord(topicConfig.getOnFailureTopic(), record,
                Instant.now().plus(topicConfig.getRetryInterval()));
        }

        private TopicConfig getConfig(PartitionContext partitionContext) {
            return topicService.getConfig(new Topic(partitionContext.getTopicPartition().topic(), kafkaClusterType));
        }
    }
}

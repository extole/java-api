package com.extole.common.event.kafka.consumer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Strings;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.GroupIdNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.extole.common.event.BatchEventListener;
import com.extole.common.event.EventListener;
import com.extole.common.event.KafkaAdminClient;
import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.ConsumerShutdownContext;
import com.extole.common.event.kafka.ExtoleKafkaConsumer;
import com.extole.common.event.kafka.KafkaEventConsumerContext;
import com.extole.common.event.kafka.KafkaEventConsumerMissingAttributeException;
import com.extole.common.event.kafka.KafkaEventConsumerRuntimeException;
import com.extole.common.event.kafka.producer.InternalRetryEventProducer;
import com.extole.common.event.kafka.serializer.json.JsonDecoder;
import com.extole.common.event.topic.TopicService;
import com.extole.common.lang.ToString;
import com.extole.common.lang.date.ExtoleTimeModule;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.id.IdGenerator;
import com.extole.spring.StopFirst;

@Component
public class KafkaEventConsumerFactory implements StopFirst {
    public static final String TOPIC_POSTFIX_DEAD = "_dead";
    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventConsumerFactory.class);
    private static final IdGenerator ID_GENERATOR = new IdGenerator();

    private final Map<ConsumerKey, ExtoleKafkaConsumer<?>> builtConsumers = new ConcurrentHashMap<>();
    private final Set<String> myGlobalConsumerGroups = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final TopicService topicService;
    private final List<String> globalBootstrapServers;
    private final List<String> localBootstrapServers;
    private final String serializerClassDefault;
    private final Long fetchMinBytesDefault;
    private final Long fetchMaxBytesDefault;
    private final Long fetchMaxWaitMsDefault;
    private final Long maxPartitionFetchBytesDefault;
    private final Long maxPollRecordsDefault;
    private final Long maxPollIntervalMsDefault;
    private final Long requestTimeoutMsDefault;
    private final Long sessionTimeoutMsDefault;
    private final String autoOffsetResetDefault;
    private final ExtoleMetricRegistry extoleMetricsRegistry;
    private final InternalRetryEventProducer kafkaEventProducer;
    private final Duration maxPollBlockWaitDefault;
    private final int maxPartitionsDefault;
    private final long processingTimeoutMsDefault;
    private final int inMemoryPartitionEventMaxDefault;
    private final long batchWaitMaxMsDefault;
    private final IsolationLevel isolationLevelDefault;
    private final String instanceName;
    private final String groupIdTemplate;
    private final String broadcastGroupIdTemplate;
    private final KafkaAdminClient kafkaAdminClient;
    private final long performanceLoggerLogThresholdMs;
    private final AtomicBoolean isHealthy = new AtomicBoolean(true);
    private final KafkaConsumerRecordProcessor kafkaConsumerRecordProcessor;

    @Autowired
    public KafkaEventConsumerFactory(TopicService topicService,
        @Value("${extole.instance.name:lo}") String instanceName,
        @Value("${kafka.global.consumer.bootstrap.servers:kafka-private.${extole.environment:lo}.intole.net:9092}") String globalBootstrapServers,
        @Value("${kafka.consumer.bootstrap.servers:kafka-${aws.availability.zone:}-private.${extole.environment:lo}"
            + ".intole.net:9092}") String localBootstrapServers,
        @Value("${kafka.serializer.class:org.apache.kafka.common.serialization.StringDeserializer}") String serializerClass,
        @Value("${kafka.consumer.fetch.min.bytes:1}") Long fetchMinBytes,
        @Value("${kafka.consumer.fetch.max.bytes:62914560}") Long fetchMaxBytes,
        @Value("${kafka.consumer.fetch.max.wait.ms:500}") Long fetchMaxWaitMs,
        @Value("${kafka.consumer.max.partition.fetch.bytes:1048576}") Long maxPartitionFetchBytes,
        @Value("${kafka.consumer.max.poll.records:500}") Long maxPollRecords,
        @Value("${kafka.consumer.max.poll.interval.ms:300000}") Long maxPollIntervalMs,
        @Value("${kafka.consumer.request.timeout.ms:5000}") Long requestTimeoutMs,
        @Value("${kafka.consumer.session.timeout.ms:45000}") Long sessionTimeoutMs,
        @Value("${kafka.consumer.auto.offset.reset:earliest}") String autoOffsetReset,
        @Value("${kafka.consumer.max.poll.block.wait.ms:5000}") long maxPollBlockWait,
        @Value("${kafka.consumer.max.partitions:500}") int maxPartitions,
        @Value("${kafka.consumer.processing.timeout.ms:30000}") long processingTimeoutMs,
        @Value("${kafka.consumer.in.memory.partition.event.max:500}") int inMemoryPartitionEventMax,
        @Value("${kafka.consumer.batch.wait.max.ms:50}") long batchWaitMaxMs,
        @Value("${kafka.consumer.isolation_level:read_committed}") String isolationLevel,
        @Value("${kafka.consumer.performanceLogger.log.threshold.ms:10000}") long performanceLoggerLogThresholdMs,
        ApplicationContext applicationContext,
        ExtoleMetricRegistry extoleMetricsRegistry,
        InternalRetryEventProducer kafkaEventProducer,
        KafkaConsumerRecordProcessor kafkaConsumerRecordProcessor,
        KafkaAdminClient kafkaAdminClient) {
        this.topicService = topicService;
        this.extoleMetricsRegistry = extoleMetricsRegistry;
        this.globalBootstrapServers = Arrays.asList(globalBootstrapServers.split(","));
        this.localBootstrapServers = Arrays.asList(localBootstrapServers.split(","));
        this.fetchMinBytesDefault = fetchMinBytes;
        this.fetchMaxBytesDefault = fetchMaxBytes;
        this.fetchMaxWaitMsDefault = fetchMaxWaitMs;
        this.maxPartitionFetchBytesDefault = maxPartitionFetchBytes;
        this.maxPollRecordsDefault = maxPollRecords;
        String appName = applicationContext.getApplicationName();
        String trimmedAppName;
        if (!Strings.isNullOrEmpty(appName)) {
            trimmedAppName = appName.substring(1);
        } else {
            trimmedAppName = "unspecified";
        }
        groupIdTemplate = trimmedAppName + "-%s";
        broadcastGroupIdTemplate = groupIdTemplate + "-" + instanceName + "-%s";

        this.maxPollIntervalMsDefault = maxPollIntervalMs;
        this.requestTimeoutMsDefault = requestTimeoutMs;
        this.sessionTimeoutMsDefault = sessionTimeoutMs;
        this.serializerClassDefault = serializerClass;
        this.autoOffsetResetDefault = autoOffsetReset;
        this.kafkaEventProducer = kafkaEventProducer;
        this.instanceName = instanceName;
        this.maxPollBlockWaitDefault = Duration.ofMillis(maxPollBlockWait);
        this.maxPartitionsDefault = maxPartitions;
        this.processingTimeoutMsDefault = processingTimeoutMs;
        this.inMemoryPartitionEventMaxDefault = inMemoryPartitionEventMax;
        this.batchWaitMaxMsDefault = batchWaitMaxMs;
        this.isolationLevelDefault = IsolationLevel.valueOf(isolationLevel.toUpperCase());
        this.kafkaConsumerRecordProcessor = kafkaConsumerRecordProcessor;
        this.kafkaAdminClient = kafkaAdminClient;
        this.performanceLoggerLogThresholdMs = performanceLoggerLogThresholdMs;
    }

    public boolean isHealthy() {
        return isHealthy.get();
    }

    public <T> KafkaEventConsumerBuilder<T> createBuilder() {
        return new KafkaEventConsumerBuilder<>();
    }

    @Override
    public void stop() {
        LOG.warn("Shutting down kafkaConsumers for the webapp");
        shutdown.set(true);

        ExecutorService executorService = Executors.newCachedThreadPool();
        Map<String, Future<?>> topics = new HashMap<>();

        for (ExtoleKafkaConsumer<?> consumer : builtConsumers.values()) {
            topics.put(consumer.getGroupId(),
                executorService.submit(() -> {
                    consumer.shutdown();
                }));
        }
        topics.forEach((topic, future) -> {
            try {
                future.get();
            } catch (Exception e) {
                LOG.error("Failed to wait for Consumer {} to shutdown", topic, e);
            }
        });
        executorService.shutdownNow();

        if (!myGlobalConsumerGroups.isEmpty()) {
            LOG.info("Shutdown process deleting broadcast consumer groups: {}", myGlobalConsumerGroups);
            for (String globalBootstrapServer : globalBootstrapServers) {
                AdminClient adminClient = kafkaAdminClient.getInstance(KafkaClusterType.GLOBAL, globalBootstrapServer);
                Set<Map.Entry<String, KafkaFuture<Void>>> tasksToDeleteGroups = adminClient
                    .deleteConsumerGroups(myGlobalConsumerGroups)
                    .deletedGroups().entrySet();
                LOG.info("Deleting consumer groups: {}", Integer.valueOf(myGlobalConsumerGroups.size()));
                for (Map.Entry<String, KafkaFuture<Void>> entry : tasksToDeleteGroups) {
                    try {
                        entry.getValue().get();
                    } catch (ExecutionException e) {
                        if (e.getCause() instanceof GroupIdNotFoundException) {
                            LOG.info("consumer group {} never initialized. skipping deletion", entry.getKey());
                        } else {
                            LOG.error("Failed to delete consumer group {} during shutdown", entry.getKey(), e);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LOG.error("Failed to delete consumer group {} during shutdown", entry.getKey(), e);
                    }
                }
            }
        }
    }

    public List<ExtoleKafkaConsumer> getConsumers(Topic topic) {
        List<String> bootstrapServers = KafkaClusterType.GLOBAL == topic.getClusterType() ? globalBootstrapServers
            : localBootstrapServers;
        List<ExtoleKafkaConsumer> kafkaConsumers = new ArrayList<>();
        for (String bootstrapServer : bootstrapServers) {
            builtConsumers.entrySet().stream()
                .filter(entry -> entry.getKey().getTopicName().startsWith(topic.getName())
                    && entry.getKey().getServer().equals(bootstrapServer))
                .map(Map.Entry::getValue)
                .forEach(kafkaConsumers::add);
        }
        return kafkaConsumers;
    }

    public class KafkaEventConsumerBuilder<T> {
        private final AtomicBoolean initialized = new AtomicBoolean();
        private Topic topic;
        private String groupId;
        private String serializerClass = serializerClassDefault;
        private Long fetchMinBytes = fetchMinBytesDefault;
        private Long fetchMaxBytes = fetchMaxBytesDefault;
        private Long fetchMaxWaitMs = fetchMaxWaitMsDefault;
        private Long maxPartitionFetchBytes = maxPartitionFetchBytesDefault;
        private Long maxPollIntervalMs = maxPollIntervalMsDefault;
        private Long requestTimeoutMs = requestTimeoutMsDefault;
        private Long sessionTimeoutMs = sessionTimeoutMsDefault;
        private String autoOffsetReset = autoOffsetResetDefault;
        private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new ExtoleTimeModule())
            .registerModule(new Jdk8Module())
            .registerModule(new GuavaModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        private Class<T> classType;
        private Long maxPollRecords = maxPollRecordsDefault;
        private Duration maxPollBlockWait = maxPollBlockWaitDefault;
        private int maxPartitions = maxPartitionsDefault;
        private long processingTimeoutMs = processingTimeoutMsDefault;
        private int inMemoryPartitionEventMax = inMemoryPartitionEventMaxDefault;
        private long batchWaitMaxMs = batchWaitMaxMsDefault;
        private boolean isBroadcastConsumer = false;
        private Optional<Long> partitionLookbackPeriodMs = Optional.empty();
        private Optional<Function<KafkaEventConsumerContext, Boolean>> isShutdownRequiredClosure = Optional.empty();
        private Optional<Consumer<ConsumerShutdownContext>> onShutdownCallback = Optional.empty();
        private IsolationLevel isolationLevel = isolationLevelDefault;

        public KafkaEventConsumerBuilder() {
        }

        public KafkaEventConsumerBuilder<T> withClassType(Class<T> classType) {
            this.classType = classType;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withTopic(Topic topic) {
            this.topic = topic;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withIsolationLevel(IsolationLevel isolationLevel) {
            this.isolationLevel = isolationLevel;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withBroadcastGroup() {
            this.isBroadcastConsumer = true;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withSerializerClass(String serializerClass) {
            this.serializerClass = serializerClass;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withFetchMinBytes(Long fetchMinBytes) {
            this.fetchMinBytes = fetchMinBytes;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withFetchMaxBytes(Long fetchMaxBytes) {
            this.fetchMaxBytes = fetchMaxBytes;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withFetchMaxWaitMs(Long fetchMaxWaitMs) {
            this.fetchMaxWaitMs = fetchMaxWaitMs;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withMaxPartitionFetchBytes(Long maxPartitionFetchBytes) {
            this.maxPartitionFetchBytes = maxPartitionFetchBytes;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withMaxPollRecords(Long maxPollRecords) {
            this.maxPollRecords = maxPollRecords;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withMaxPollIntervalMs(Long maxPollIntervalMs) {
            this.maxPollIntervalMs = maxPollIntervalMs;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withRequestTimeoutMs(Long requestTimeoutMs) {
            this.requestTimeoutMs = requestTimeoutMs;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withSessionTimeoutMs(Long sessionTimeoutMs) {
            this.sessionTimeoutMs = sessionTimeoutMs;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withAutoOffsetReset(String autoOffsetReset) {
            this.autoOffsetReset = autoOffsetReset;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withMaxPollBlockWait(Duration maxPollBlockWait) {
            this.maxPollBlockWait = maxPollBlockWait;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withMaxPartitions(int maxPartitions) {
            this.maxPartitions = maxPartitions;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withProcessingTimeoutMs(long processingTimeoutMs) {
            this.processingTimeoutMs = processingTimeoutMs;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withInMemoryPartitionEventMax(int inMemoryPartitionEventMax) {
            this.inMemoryPartitionEventMax = inMemoryPartitionEventMax;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withBatchWaitMaxMs(long batchWaitMaxMs) {
            this.batchWaitMaxMs = batchWaitMaxMs;
            return this;
        }

        public KafkaEventConsumerBuilder<T> withPartitionLookbackPeriodMs(long partitionLookbackPeriodMs) {
            this.partitionLookbackPeriodMs = Optional.of(Long.valueOf(partitionLookbackPeriodMs));
            return this;
        }

        public KafkaEventConsumerBuilder<T> withIsShutdownRequiredClosure(
            Function<KafkaEventConsumerContext, Boolean> isShutdownRequiredClosure) {
            this.isShutdownRequiredClosure = Optional.ofNullable(isShutdownRequiredClosure);
            return this;
        }

        public KafkaEventConsumerBuilder<T>
            withOnShutdownCallback(Consumer<ConsumerShutdownContext> onShutdownCallback) {
            this.onShutdownCallback = Optional.ofNullable(onShutdownCallback);
            return this;
        }

        public List<KafkaEventConsumer<T>> startBatchListener(BatchEventListener<T> batchEventListener) {
            List<KafkaEventConsumer<T>> consumers = startConsumer();
            consumers.forEach(consumer -> consumer.startup(batchEventListener));
            return consumers;
        }

        public void startListener(EventListener<T> eventListener) {
            List<KafkaEventConsumer<T>> consumers = startConsumer();
            consumers.forEach(consumer -> consumer.startup(eventListener));
        }

        public List<KafkaEventConsumer<T>> build() {
            if (shutdown.get()) {
                throw new KafkaEventConsumerRuntimeException(
                    "Kafka Consumers are shutting down! Cannot start another Kafka consumer for topic: " + topic);
            }

            String clientId = instanceName + "-" + topic.getName() + "-" + ID_GENERATOR.generateId();
            List<String> bootstrapServers =
                KafkaClusterType.GLOBAL == topic.getClusterType() ? globalBootstrapServers
                    : localBootstrapServers;
            List<KafkaEventConsumer<T>> consumers = new ArrayList<>();
            for (String bootstrapServer : bootstrapServers) {
                Properties properties = initializeProperties(topic, bootstrapServer);
                String derivedGroupId = deriveConsumerGroupId();
                properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
                properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords.toString());
                properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, derivedGroupId);
                properties.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG,
                    isolationLevel.toString().toLowerCase(Locale.ROOT));
                properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
                KafkaEventConsumer<T> consumer = new KafkaEventConsumer<>(topicService,
                    topic,
                    new KafkaConsumer<>(properties),
                    derivedGroupId,
                    clientId,
                    new JsonDecoder<>(classType, objectMapper),
                    extoleMetricsRegistry,
                    kafkaEventProducer,
                    kafkaConsumerRecordProcessor,
                    maxPollBlockWait,
                    processingTimeoutMs,
                    maxPartitions,
                    inMemoryPartitionEventMax,
                    batchWaitMaxMs, performanceLoggerLogThresholdMs,
                    partitionLookbackPeriodMs,
                    isShutdownRequiredClosure,
                    onShutdownCallback,
                    bootstrapServer,
                    isHealthy);
                builtConsumers.put(new ConsumerKey(topic.getName(), clientId, bootstrapServer), consumer);
                consumers.add(consumer);
            }
            return consumers;
        }

        private List<KafkaEventConsumer<T>> startConsumer() {
            if (!initialized.compareAndSet(false, true)) {
                throw new KafkaEventConsumerRuntimeException(
                    "Kafka Consumer has already been initialized! Cannot start another for topic "
                        + topic);
            }
            if (topic == null) {
                throw new KafkaEventConsumerMissingAttributeException(
                    "Consumer initialized without required attribute: topic");
            }
            return build();
        }

        private String deriveConsumerGroupId() {
            String consumerGroupId;
            if (Strings.isNullOrEmpty(groupId)) {
                if (isBroadcastConsumer) {
                    consumerGroupId =
                        String.format(broadcastGroupIdTemplate, topic.getName(),
                            String.valueOf(Instant.now().toEpochMilli()));
                    myGlobalConsumerGroups.add(consumerGroupId);
                } else {
                    consumerGroupId = String.format(groupIdTemplate, topic.getName());
                }
            } else {
                consumerGroupId = groupId;
            }
            return consumerGroupId;
        }

        private Properties initializeProperties(Topic consumerTopic, String bootstrapServer) {
            if (isBroadcastConsumer && !Strings.isNullOrEmpty(groupId)) {
                throw new KafkaEventConsumerRuntimeException(
                    "Consumer cannot specify groupId and be a broadcast consumer");
            }

            if (Strings.isNullOrEmpty(bootstrapServer)) {
                throw new KafkaEventConsumerMissingAttributeException(
                    "Consumer " + consumerTopic + " missing attribute: bootstrapServers");
            }

            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, serializerClass);
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, serializerClass);
            properties.setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes.toString());
            properties.setProperty(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes.toString());
            properties.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs.toString());
            properties.setProperty(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes.toString());
            properties.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs.toString());
            properties.setProperty(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs.toString());
            properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs.toString());
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
            properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                CooperativeStickyAssignor.class.getName());
            return properties;
        }
    }

    private static class ConsumerKey {
        private final String topicName;
        private final String clientId;
        private final String server;

        ConsumerKey(String topicName, String clientId, String server) {
            this.topicName = topicName;
            this.clientId = clientId;
            this.server = server;
        }

        public String getTopicName() {
            return topicName;
        }

        public String getServer() {
            return server;
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }
            ConsumerKey consumerKey = (ConsumerKey) that;
            return topicName.equals(consumerKey.topicName) && clientId.equals(consumerKey.clientId)
                && server.equals(consumerKey.server);
        }

        @Override
        public int hashCode() {
            return Objects.hash(topicName, clientId, server);
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }
    }
}

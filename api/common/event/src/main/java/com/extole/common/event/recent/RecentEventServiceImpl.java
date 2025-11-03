package com.extole.common.event.recent;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsOptions;
import org.apache.kafka.clients.admin.ListOffsetsOptions;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.event.CloseableIterator;
import com.extole.common.event.KafkaAdminClient;
import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.serializer.exception.DeserializerRuntimeException;
import com.extole.common.event.migration.EventMigrationService;
import com.extole.common.event.migration.EventMigrationServiceException;
import com.extole.common.event.migration.MigrationObjectMapper;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

@Component
public class RecentEventServiceImpl implements RecentEventService {
    private static final Logger LOG = LoggerFactory.getLogger(RecentEventServiceImpl.class);

    private static final Comparator<ConsumerRecord<String, String>> RECORD_COMPARATOR =
        Comparator.comparing(ConsumerRecord::timestamp);

    private final String instanceName;
    private final String globalBootstrapServers;
    private final String localBootstrapServers;
    private final String serializerClass;
    private final Duration maxPollDuration;
    private final KafkaAdminClient kafkaAdminClient;
    private final long requestTimeoutMs;

    public RecentEventServiceImpl(
        @Value("${kafka.recent.event.service.impl.max.poll.duration.ms:4000}") long maxPollDurationMs,
        @Value("${extole.instance.name:lo}") String instanceName,
        @Value("${kafka.global.consumer.bootstrap.servers:kafka-private.${extole.environment:lo}.intole.net:"
            + "9092}") String globalBootstrapServers,
        @Value("${kafka.consumer.bootstrap.servers:kafka-${aws.availability.zone:}-private.${extole.environment:lo}"
            + ".intole.net:9092}") String localBootstrapServers,
        @Value("${kafka.serializer.class:"
            + "org.apache.kafka.common.serialization.StringDeserializer}") String serializerClass,
        @Value("${kafka.poll.topic.names.request.timeout.ms:30000}") long requestTimeoutMs,
        KafkaAdminClient kafkaAdminClient) {
        this.instanceName = instanceName;
        this.globalBootstrapServers = globalBootstrapServers;
        this.localBootstrapServers = localBootstrapServers;
        this.serializerClass = serializerClass;
        this.maxPollDuration = Duration.ofMillis(maxPollDurationMs);
        this.kafkaAdminClient = kafkaAdminClient;
        this.requestTimeoutMs = requestTimeoutMs;
    }

    @Override
    public <E> RecentEventQueryBuilder<E> createRecentEventQuery(Topic topic, Class<E> classType, Id<?> sourceId) {
        return new RecentEventQueryBuilderImpl<>(topic, classType, sourceId);
    }

    @Override
    public TopicSummary getTopicSummary(Topic topic, String groupId)
        throws KafkaFetchException, KafkaTopicNotFoundException {
        try (AdminClient adminClient = createKafkaAdminClient()) {
            Integer listOffsetTimeout = Integer.valueOf(Long.valueOf(requestTimeoutMs).intValue());
            Set<String> kafkaTopics =
                adminClient.listTopics(new ListTopicsOptions().timeoutMs(listOffsetTimeout))
                    .names()
                    .get(requestTimeoutMs, TimeUnit.MILLISECONDS);
            if (!kafkaTopics.contains(topic.getName())) {
                LOG.warn("Unable to find topic: {}. Existing topics: {}", topic, kafkaTopics);
                throw new KafkaTopicNotFoundException("Topic not found: " + topic.getName(), topic.getName());
            }

            Map<TopicPartition, OffsetAndMetadata> offsetsAndMetadata =
                adminClient.listConsumerGroupOffsets(groupId,
                    new ListConsumerGroupOffsetsOptions().timeoutMs(listOffsetTimeout))
                    .partitionsToOffsetAndMetadata()
                    .get(requestTimeoutMs, TimeUnit.MILLISECONDS);

            Map<TopicPartition, OffsetSpec> topicPartitionOffsetSpec = offsetsAndMetadata.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, item -> OffsetSpec.latest()));

            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> endOffsets =
                adminClient.listOffsets(topicPartitionOffsetSpec, new ListOffsetsOptions().timeoutMs(listOffsetTimeout))
                    .all().get(requestTimeoutMs, TimeUnit.MILLISECONDS);

            Map<TopicPartition, PartitionMetadata> partitionsMetadata = offsetsAndMetadata.entrySet().stream()
                .map(item -> new PartitionMetadata(item.getKey(), item.getValue().offset(), Optional
                    .ofNullable(endOffsets.get(item.getKey()))
                    .map(endOffset -> Long.valueOf(endOffset.offset()))
                    .orElse(Long.valueOf(0L))
                    .longValue()))
                .collect(Collectors.toMap(PartitionMetadata::getTopicPartition, Function.identity()));

            return partitionsMetadata.entrySet().stream()
                .reduce(new TopicSummary(topic.getName(), 0, 0),
                    (topicSummary, partitionMetadata) -> new TopicSummary(topic.getName(),
                        topicSummary.getProcessedEvents() + partitionMetadata.getValue().getCurrentOffset(),
                        topicSummary.getTotalEvents() + partitionMetadata.getValue().getLongEndOffset()),
                    ((metadata1, metadata2) -> new TopicSummary(metadata1.getTopic(),
                        metadata1.getProcessedEvents() + metadata2.getProcessedEvents(),
                        metadata1.getTotalEvents() + metadata2.getTotalEvents())));
        } catch (ExecutionException | TimeoutException e) {
            throw new KafkaFetchException("Unable to fetch metadata for topic: " + topic.getName(), topic.getName(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaFetchException("Interrupted when fetching metadata for topic: " + topic.getName(),
                topic.getName(), e);
        }
    }

    private AdminClient createKafkaAdminClient() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, localBootstrapServers);
        properties.setProperty(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, String.valueOf(requestTimeoutMs));
        return AdminClient.create(properties);
    }

    private final class RecentEventQueryBuilderImpl<T> implements RecentEventQueryBuilder<T> {

        private final Topic topic;
        private final Class<T> classType;
        private final Id<?> sourceId;
        private Optional<EventMigrationService> eventMigrationService = Optional.empty();

        private RecentEventQueryBuilderImpl(Topic topic, Class<T> classType, Id<?> sourceId) {
            this.topic = topic;
            this.classType = classType;
            this.sourceId = sourceId;
        }

        @Override
        public RecentEventQueryBuilder<T> withEventMigrationService(EventMigrationService eventMigrationService) {
            this.eventMigrationService = Optional.of(eventMigrationService);
            return this;
        }

        @Override
        public CloseableIterator<T> query() {
            LOG.debug("Recent event query for topic {} with sourceId {}", topic, sourceId);
            Properties properties = constructKafkaProperties();
            // TODO support with multiple clusters ENG-XXXX
            AdminClient adminClient;
            Optional<AdminClient> existingAdminClient =
                kafkaAdminClient.getAll(topic.getClusterType()).values().stream().findFirst();
            if (existingAdminClient.isPresent()) {
                adminClient = existingAdminClient.get();
            } else {
                String bootstrapServers =
                    topic.getClusterType() == KafkaClusterType.GLOBAL ? globalBootstrapServers : localBootstrapServers;
                adminClient = kafkaAdminClient.getInstance(topic.getClusterType(), bootstrapServers);
            }

            Set<TopicPartition> partitions = new HashSet<>();
            try {
                Optional<TopicDescription> topicDescription = Optional.ofNullable(adminClient.describeTopics(
                    Collections.singleton(topic.getName())).all().get().get(topic.getName()));
                if (!topicDescription.isPresent()) {
                    return CloseableIterator.wrap(Collections.emptyIterator());
                }
                for (TopicPartitionInfo info : topicDescription.get().partitions()) {
                    partitions.add(new TopicPartition(topic.getName(), info.partition()));
                }
            } catch (ExecutionException e) {
                if (e.getCause() == null
                    || !e.getCause().getClass().isAssignableFrom(UnknownTopicOrPartitionException.class)) {
                    LOG.error("Failed to retrieve topic information for {}", topic, e);
                }
                return CloseableIterator.wrap(Collections.emptyIterator());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return CloseableIterator.wrap(Collections.emptyIterator());
            }

            return new RecentEventIterator(properties, adminClient, partitions);
        }

        private Properties constructKafkaProperties() {
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "0");
            properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
            String uniqueGroupId = instanceName + "-" + topic + "-" + sourceId;
            properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, uniqueGroupId);
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, uniqueGroupId);
            if (topic.getClusterType().equals(KafkaClusterType.EDGE)) {
                properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, localBootstrapServers);
            } else {
                properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, globalBootstrapServers);
            }
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, serializerClass);
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, serializerClass);
            properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            return properties;
        }

        private final class RecentEventIterator implements CloseableIterator<T> {

            private final Properties properties;
            private final AdminClient adminClient;
            private final KafkaConsumer<String, String> consumer;
            private final Map<TopicPartition, Long> beginningOffsets;
            private final PriorityQueue<ConsumerRecord<String, String>> nextRecords;

            private RecentEventIterator(Properties properties, AdminClient adminClient,
                Set<TopicPartition> allPartitions) {
                this.properties = properties;
                this.adminClient = adminClient;
                this.consumer = new KafkaConsumer<>(properties);
                try {
                    this.consumer.assign(allPartitions);
                    List<TopicPartition> nonEmptyPartitions = new ArrayList<>(allPartitions);

                    this.beginningOffsets = consumer.beginningOffsets(allPartitions);
                    Map<TopicPartition, Long> endOffsets = consumer.endOffsets(allPartitions);
                    consumer.pause(allPartitions);

                    for (Map.Entry<TopicPartition, Long> endOffset : endOffsets.entrySet()) {
                        if (endOffset.getValue() != null
                            && endOffset.getValue() > beginningOffsets.get(endOffset.getKey())) {
                            consumer.seek(endOffset.getKey(), endOffset.getValue() - 1);
                        } else {
                            nonEmptyPartitions.remove(endOffset.getKey());
                        }
                    }

                    if (nonEmptyPartitions.isEmpty()) {
                        nextRecords = new PriorityQueue<>();
                        LOG.info("event query for topic {} found no partitions", topic);
                        return;
                    }

                    nextRecords = new PriorityQueue<>(nonEmptyPartitions.size(), RECORD_COMPARATOR.reversed());
                    for (TopicPartition partition : nonEmptyPartitions) {
                        consumer.resume(Collections.singleton(partition));
                        List<ConsumerRecord<String, String>> records =
                            consumer.poll(maxPollDuration).records(partition);
                        if (!records.isEmpty()) {
                            nextRecords.add(records.get(0));
                        } else {
                            LOG.warn("Failed to retrieve detected record for topic {} partition {} after waiting {}ms",
                                topic, partition, maxPollDuration);
                        }
                        consumer.pause(Collections.singleton(partition));
                    }
                } catch (RuntimeException e) {
                    closeConsumer();
                    throw e;
                }
            }

            @Override
            public boolean hasNext() {
                return !nextRecords.isEmpty();
            }

            @Override
            public T next() {
                ConsumerRecord<String, String> latestRecord = nextRecords.poll();
                LOG.info("initialRecords {} latestRecord {}", nextRecords, latestRecord);
                if (latestRecord == null) {
                    throw new NoSuchElementException();
                }

                T latestEvent = mapToEvent(latestRecord);
                TopicPartition partition = new TopicPartition(topic.getName(), latestRecord.partition());
                if (latestRecord.offset() > beginningOffsets.get(partition)) {
                    nextRecord(partition, latestRecord.offset()).ifPresent(record -> nextRecords.add(record));
                }
                if (nextRecords.isEmpty()) {
                    LOG.info("reached end of partition {} at offset", partition.partition(), latestRecord.offset());
                }

                return latestEvent;
            }

            @Override
            public void close() {
                closeConsumer();
            }

            private Optional<ConsumerRecord<String, String>> nextRecord(TopicPartition partition, long currentOffset) {
                consumer.resume(Collections.singleton(partition));
                consumer.seek(partition, currentOffset - 1);
                List<ConsumerRecord<String, String>> records = consumer.poll(maxPollDuration).records(partition);
                consumer.pause(Collections.singleton(partition));
                if (records.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(records.get(0));
            }

            private T mapToEvent(ConsumerRecord<String, String> record) {
                String eventValue = eventMigrationService.map(service -> {
                    try {
                        return service.migrate(record.value()).get(0);
                    } catch (EventMigrationServiceException e) {
                        throw new DeserializerRuntimeException(
                            "Failed to migrate record: " + record + ". Expected class type: " + classType, e);
                    }
                }).orElse(record.value());

                try {
                    return MigrationObjectMapper.OBJECT_MAPPER.readValue(eventValue.getBytes(), classType);
                } catch (IOException e) {
                    throw new DeserializerRuntimeException(
                        "Failed to decode record: " + record + " to class type: " + classType, e);
                }
            }

            private void closeConsumer() {
                consumer.close();
                adminClient.deleteConsumerGroups(
                    Collections.singletonList(properties.getProperty(ConsumerConfig.GROUP_ID_CONFIG)));
            }

        }
    }

    private static class PartitionMetadata {
        private final TopicPartition topicPartition;
        private final long currentOffset;
        private final long longEndOffset;

        PartitionMetadata(TopicPartition topicPartition, long currentOffset, long longEndOffset) {
            this.topicPartition = topicPartition;
            this.currentOffset = currentOffset;
            this.longEndOffset = longEndOffset;
        }

        public TopicPartition getTopicPartition() {
            return topicPartition;
        }

        public long getCurrentOffset() {
            return currentOffset;
        }

        public long getLongEndOffset() {
            return longEndOffset;
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }
    }
}

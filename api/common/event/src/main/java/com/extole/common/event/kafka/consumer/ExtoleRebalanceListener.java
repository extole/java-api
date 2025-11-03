package com.extole.common.event.kafka.consumer;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.event.KafkaClusterType;
import com.extole.common.metrics.ExtoleCounter;
import com.extole.common.metrics.ExtoleHistogram;
import com.extole.common.metrics.ExtoleMetricRegistry;

class ExtoleRebalanceListener implements ConsumerRebalanceListener {
    private static final Logger LOG = LoggerFactory.getLogger(ExtoleRebalanceListener.class);

    private final Consumer<String, String> consumer;
    private final String clientId;
    private final ExtoleHistogram partitionsRevokedProcessingDurationHistogram;
    private final Map<TopicPartition, PartitionContext> partitionContexts;
    private final long processingTimeoutMs;
    private final Optional<Long> lookbackPeriodMs;
    private final KafkaClusterType kafkaClusterType;
    private final String bootstrapServer;
    private final ExtoleMetricRegistry metricRegistry;
    private final Map<TopicPartition, Long> partitionOffsetsBeforeLookback;

    ExtoleRebalanceListener(Consumer<String, String> consumer, String groupId, String clientId,
        ExtoleMetricRegistry metricRegistry, Map<TopicPartition, PartitionContext> partitionContexts,
        long processingTimeoutMs, Optional<Long> partitionLookbackPeriodMs,
        KafkaClusterType kafkaClusterType, String bootstrapServer,
        Map<TopicPartition, Long> partitionOffsetsBeforeLookback) {
        this.consumer = consumer;
        this.clientId = clientId;
        String metricPrefix = KafkaEventConsumer.METRIC_PREFIX + "." + groupId;
        this.metricRegistry = metricRegistry;
        this.partitionsRevokedProcessingDurationHistogram =
            metricRegistry.histogram(metricPrefix + ".partitions.revoked.processing.duration");
        this.partitionContexts = partitionContexts;
        this.processingTimeoutMs = processingTimeoutMs;
        this.lookbackPeriodMs = partitionLookbackPeriodMs;
        this.kafkaClusterType = kafkaClusterType;
        this.bootstrapServer = bootstrapServer;
        this.partitionOffsetsBeforeLookback = partitionOffsetsBeforeLookback;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        Instant partitionRevokeTime = Instant.now();
        try {
            Instant revokeTimeout = partitionRevokeTime.plus(Duration.ofMillis(processingTimeoutMs));
            Set<TopicPartition> stillProcessingPartitions = new HashSet<>(partitions);

            while (Instant.now().isBefore(revokeTimeout) && !stillProcessingPartitions.isEmpty()) {
                Iterator<TopicPartition> partitionIterator = stillProcessingPartitions.iterator();
                while (partitionIterator.hasNext()) {
                    TopicPartition partition = partitionIterator.next();
                    PartitionContext partitionContext = partitionContexts.get(partition);
                    if (partitionContext == null) {
                        LOG.info("Consumer {} removing unused partition {}", clientId, partition);
                        partitionIterator.remove();
                        getRevokedCounter(partition.topic()).increment();
                    } else if (partitionContext.isDone()) {
                        commitIfNecessary(partitionContext);
                        LOG.info(
                            "Removing partition {} - committed offset {}", partition,
                            String.valueOf(partitionContext.getLastProcessedOffset() + 1));
                        partitionContexts.remove(partition);
                        partitionIterator.remove();
                        getRevokedCounter(partition.topic()).increment();
                    } else if (!partitionContext.isRevoked()) {
                        LOG.info("Partition {} revoked", partition);
                        partitionContext.revoke();
                    }
                }
                if (!stillProcessingPartitions.isEmpty()) {
                    Thread.sleep(1L);
                }
            }
            if (!stillProcessingPartitions.isEmpty()) {
                LOG.error("Event processing timed out after {} after {} partitions were revoked. "
                    + "Processing will continue, but events may have been re-read for partitions {}",
                    Duration.between(partitionRevokeTime, Instant.now()), partitions, stillProcessingPartitions);
                for (TopicPartition topicPartition : stillProcessingPartitions) {
                    partitionContexts.remove(topicPartition);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Interrupted waiting for partitions to finish processing events during revoke of partitions {}",
                partitions);
        } finally {
            partitionsRevokedProcessingDurationHistogram.update(partitionRevokeTime, Instant.now());
        }
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        for (TopicPartition partition : partitions) {
            LOG.info("Partition {} assigned", partition);
            getAssignedCounter(partition.topic()).increment();
        }
        if (lookbackPeriodMs.isPresent()) {
            Long lookbackInstantMs =
                Long.valueOf(Instant.now().minusMillis(lookbackPeriodMs.get().longValue()).toEpochMilli());
            Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
            for (TopicPartition partition : partitions) {
                timestampsToSearch.put(partition, lookbackInstantMs);
            }
            partitionOffsetsBeforeLookback.putAll(consumer.committed(new HashSet<>(partitions))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(entry -> entry.getKey(), value -> Long.valueOf(value.getValue().offset()))));
            Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes = consumer.offsetsForTimes(timestampsToSearch);
            Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
            for (Map.Entry<TopicPartition, OffsetAndTimestamp> partitionOffset : offsetsForTimes.entrySet()) {
                TopicPartition partition = partitionOffset.getKey();
                if (partitionOffset.getValue() != null) {
                    long offset = partitionOffset.getValue().offset();
                    LOG.info("Partition {} seeking offset {} due to lookbackPeriod of {}ms", partition,
                        String.valueOf(offset), lookbackPeriodMs.get());
                    consumer.seek(partition, offset);
                    offsetsToCommit.put(partition, new OffsetAndMetadata(offset));
                } else {
                    LOG.info("Partition {} has no offset associated with lookbackPeriod of {}ms", partition,
                        lookbackPeriodMs.get());
                    offsetsToCommit.put(partition, new OffsetAndMetadata(consumer.position(partition)));
                }
            }
            // for low-throughput topics, commit offsets assigned to publish current state of group to broker
            consumer.commitSync(offsetsToCommit);
        }
    }

    private void commitIfNecessary(PartitionContext partitionContext) {
        Optional<OffsetAndMetadata> offsetToCommit = partitionContext.getOffsetToCommit();
        if (offsetToCommit.isPresent()) {
            try {
                consumer.commitSync(Collections.singletonMap(partitionContext.getTopicPartition(),
                    offsetToCommit.get()));
            } catch (Exception e) {
                LOG.error("Failed to commit offset {} for revoked partition {}  [topic = {}, kafka cluster type = {}, "
                    + "bootstrap server ={}] , consumer group metadata {}, partition leader = {}",
                    offsetToCommit.get(),
                    String.valueOf(partitionContext.getTopicPartition().partition()),
                    partitionContext.getTopicPartition().topic(),
                    kafkaClusterType, bootstrapServer, consumer.groupMetadata(),
                    getLeaderDetails(partitionContext.getTopicPartition()), e);
            }
        }
    }

    private String getLeaderDetails(TopicPartition partition) {
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(partition.topic());
        if (partitionInfos != null) {
            Optional<PartitionInfo> partitionInfo = partitionInfos.stream()
                .filter(value -> value.partition() == partition.partition())
                .findFirst();
            if (partitionInfo.isPresent() && partitionInfo.get().leader() != null) {
                Node leader = partitionInfo.get().leader();
                return String.format("%s:%s", String.valueOf(leader.id()), leader.host());
            }
        }
        return "unknown";
    }

    private ExtoleCounter getRevokedCounter(String topic) {
        return metricRegistry.counter(ExtoleRebalanceListener.class.getName() + "." + topic + ".partition.revoked");
    }

    private ExtoleCounter getAssignedCounter(String topic) {
        return metricRegistry.counter(ExtoleRebalanceListener.class.getName() + "." + topic + ".partition.assigned");
    }
}

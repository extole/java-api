package com.extole.common.event.kafka;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.event.KafkaAdminClient;
import com.extole.common.event.Topic;
import com.extole.common.lang.ExtoleThreadFactory;
import com.extole.spring.StartFirstStopLast;

@Component
public class KafkaTopicClientFactory implements StartFirstStopLast {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicClientFactory.class);
    private static final String SERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringDeserializer";

    private final List<KafkaTopicClient> clients = Collections.synchronizedList(new ArrayList<>());
    private final Map<Topic, Supplier<List<TopicPartitionInfo>>> topicPartitionSuppliers = new HashMap<>();
    private final KafkaAdminClient kafkaAdminClient;
    private final ExecutorService consumerExecutor;
    private final long cacheExpiryMs;

    @Autowired
    public KafkaTopicClientFactory(
        KafkaAdminClient kafkaAdminClient,
        @Value("${kafka.topic.lag.cache.expire.ms:1000}") long cacheExpiryMs) {
        this.kafkaAdminClient = kafkaAdminClient;
        this.cacheExpiryMs = cacheExpiryMs;
        this.consumerExecutor = Executors.newSingleThreadExecutor(
            new ExtoleThreadFactory(KafkaTopicClientFactory.class.getSimpleName()));
    }

    public KafkaTopicClient create(String clientGroupId, String instanceName, Topic topic,
        String topicConsumerGroupId, String bootstrapServers) {
        KafkaTopicClient topicClient =
            new KafkaTopicClient(instanceName, clientGroupId, topic, topicConsumerGroupId, bootstrapServers);
        clients.add(topicClient);
        return topicClient;
    }

    @Override
    public void stop() {
        clients.forEach(client -> client.stop());
        consumerExecutor.shutdown();
    }

    public final class KafkaTopicClient implements Serializable {

        private final Supplier<List<TopicPartitionInfo>> partitionSupplier;
        private final KafkaConsumer<Object, Object> consumer;
        private final String clientGroupId;
        private final Topic topic;
        private final String topicConsumerGroupId;
        private final String bootstrapServers;

        private KafkaTopicClient(String instanceName, String clientGroupId, Topic topic, String topicConsumerGroupId,
            String bootstrapServers) {
            this.clientGroupId = clientGroupId;
            this.topic = topic;
            this.topicConsumerGroupId = topicConsumerGroupId;

            this.consumer = new KafkaConsumer<>(constructKafkaProperties(instanceName, bootstrapServers));
            this.partitionSupplier = topicPartitionSuppliers.computeIfAbsent(topic,
                topicAsKey -> Suppliers.memoizeWithExpiration(() -> getPartitions(), cacheExpiryMs,
                    TimeUnit.MILLISECONDS));
            this.bootstrapServers = bootstrapServers;
        }

        public Map<TopicPartition, OffsetAndMetadata> getCurrentOffset() {
            try {
                return kafkaAdminClient.getInstance(topic.getClusterType(), bootstrapServers)
                    .listConsumerGroupOffsets(topicConsumerGroupId)
                    .partitionsToOffsetAndMetadata().get();
            } catch (ExecutionException e) {
                LOG.error("Failed to retrieve current offset for consumer topic", e);
                return Collections.emptyMap();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.error("Interrupted while retrieving current offset for consumer topic", e);
                return Collections.emptyMap();
            }
        }

        public Map<TopicPartition, Long> getLag() {
            try {
                List<TopicPartitionInfo> partitions = partitionSupplier.get();
                Map<TopicPartition, OffsetAndMetadata> consumerGroupOffsets =
                    kafkaAdminClient.getInstance(topic.getClusterType(), bootstrapServers)
                        .listConsumerGroupOffsets(topicConsumerGroupId)
                        .partitionsToOffsetAndMetadata().get();
                Map<TopicPartition, Long> endOffsets = getEndOffsets(consumerGroupOffsets);
                LOG.debug("topic {} consumer group {} groupOffsets {} endOffsets {}", topic.getName(),
                    topicConsumerGroupId, consumerGroupOffsets, endOffsets);
                return computeLag(partitions, consumerGroupOffsets, endOffsets);
            } catch (ExecutionException e) {
                LOG.error("Failed to retrieve lag for consumer topic", e);
                return Collections.emptyMap();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.error("Interrupted while retrieving lag for consumer topic", e);
                return Collections.emptyMap();
            }
        }

        public List<TopicPartitionInfo> getPartitions() {
            try {
                return kafkaAdminClient.getInstance(topic.getClusterType(), bootstrapServers)
                    .describeTopics(Collections.singletonList(topic.getName())).allTopicNames().get()
                    .get(topic.getName()).partitions();
            } catch (ExecutionException e) {
                LOG.error("Failed to retrieve partitions for topic {} bootstrapServers {}", topic, bootstrapServers, e);
                return Collections.emptyList();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.error("Interrupted while retrieving partitions for topic", e);
                return Collections.emptyList();
            }
        }

        public void stop() {
            consumer.close();
        }

        private Map<TopicPartition, Long> computeLag(List<TopicPartitionInfo> partitions,
            Map<TopicPartition, OffsetAndMetadata> consumerGroupOffsets,
            Map<TopicPartition, Long> endOffsets) {
            Map<TopicPartition, Long> lags = new HashMap<>();
            for (TopicPartitionInfo partition : partitions) {
                Long endOffset =
                    endOffsets.get(new TopicPartition(topic.getName(), partition.partition()));
                if (endOffset == null) {
                    endOffset = Long.valueOf(0L);
                }
                OffsetAndMetadata consumerGroupOffset = consumerGroupOffsets
                    .get(new TopicPartition(topic.getName(), partition.partition()));
                long consumedOffset = consumerGroupOffset != null ? consumerGroupOffset.offset() : 0L;
                long lag = Math.abs(endOffset.longValue() - consumedOffset);
                lags.putIfAbsent(new TopicPartition(topic.getName(), partition.partition()), Long.valueOf(lag));
            }
            return lags;
        }

        private Map<TopicPartition, Long> getEndOffsets(Map<TopicPartition, OffsetAndMetadata> consumerGroupOffsets)
            throws InterruptedException, ExecutionException {
            List<TopicPartition> topicPartitions = new ArrayList<>();
            for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : consumerGroupOffsets.entrySet()) {
                TopicPartition key = entry.getKey();
                topicPartitions.add(new TopicPartition(key.topic(), key.partition()));
            }
            return consumerExecutor.submit(() -> consumer.endOffsets(topicPartitions)).get();
        }

        private Properties constructKafkaProperties(String instanceName, String localBootstrapServers) {
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, instanceName);
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, clientGroupId);
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, localBootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, SERIALIZER_CLASS);
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SERIALIZER_CLASS);
            return properties;
        }
    }
}

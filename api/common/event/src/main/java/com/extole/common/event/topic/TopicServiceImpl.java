package com.extole.common.event.topic;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.AlterConfigsResult;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.event.KafkaAdminClient;
import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.KafkaDeleteTopicException;
import com.extole.common.event.Topic;

@Component
public class TopicServiceImpl implements TopicService {
    private static final Logger LOG = LoggerFactory.getLogger(TopicServiceImpl.class);
    public static final Duration KAFKA_DEFAULT_RETENTION_PERIOD = Duration.ofHours(168);
    public static final Duration KAFKA_RETRY_SAFE_INTERVAL = Duration.ofHours(8);

    private final Map<String, Set<String>> existingTopics = new ConcurrentHashMap<>();
    private final KafkaAdminClient kafkaAdminClient;
    private final long requestTimeoutMs;
    private final long topicDeletionTimeoutMs;
    private final BaseTopicSetDefaulter baseTopicSetDefaulter;
    private final List<TopicSetDefaulter> topicSetDefaulters;
    private final Map<String, TopicConfig> topicConfigs = new ConcurrentHashMap<>();

    @Autowired
    public TopicServiceImpl(KafkaAdminClient kafkaAdminClient,
        @Value("${kafka.admin.request.timeout.ms:10000}") long requestTimeoutMs,
        @Value("${kafka.admin.topic.deletion.timeout.ms:300000}") long topicDeletionTimeoutMs,
        BaseTopicSetDefaulter baseTopicSetDefaulter,
        List<TopicSetDefaulter> topicSetDefaulters,
        List<TopicSet> topicSets) {
        this.kafkaAdminClient = kafkaAdminClient;
        this.requestTimeoutMs = requestTimeoutMs;
        this.topicDeletionTimeoutMs = topicDeletionTimeoutMs;
        this.baseTopicSetDefaulter = baseTopicSetDefaulter;
        this.topicSetDefaulters = topicSetDefaulters;
        for (TopicConfig topicConfig : topicSets.stream().map(topicSet -> topicSet.getTopicConfigs())
            .flatMap(Collection::stream).collect(Collectors.toList())) {
            LOG.info("Initialized topicConfig for topic={}, topicConfig={}", topicConfig.getTopic(), topicConfig);
            setTopicRetention(topicConfig);
            topicConfigs.put(topicConfig.getTopic().getName(), topicConfig);
        }
    }

    @Override
    public void validateTopic(Topic topic, String bootstrapServers) throws InterruptedException, ExecutionException {
        Set<String> topics = existingTopics.computeIfAbsent(bootstrapServers,
            servers -> retrieveTopics(topic.getClusterType(), servers));
        if (!topics.contains(topic.getName())) {
            TopicConfig topicConfig = getConfig(topic);
            try {
                LOG.info("Creating new topic {}", topicConfig);
                kafkaAdminClient.getInstance(topic.getClusterType(), bootstrapServers)
                    .createTopics(
                        Collections.singleton(new NewTopic(topic.getName(), topicConfig.getDesiredPartitionCount(),
                            topicConfig.getDesiredReplicationFactor())))
                    .all().get();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof TopicExistsException) {
                    LOG.info("topic {} already exists", topic);
                } else {
                    throw e;
                }
            }
            setTopicRetention(topicConfig);
            topics.add(topic.getName());
        }
    }

    private Set<String> listConsumers(List<String> groupIds, Topic topic) {
        if (groupIds.isEmpty()) {
            return Collections.emptySet();
        }
        Collection<AdminClient> adminClients = kafkaAdminClient.getAll(topic.getClusterType()).values();
        Set<String> consumerGroups = new HashSet<>();
        for (AdminClient adminClient : adminClients) {
            try {
                DescribeConsumerGroupsResult describeConsumerGroupsResult =
                    adminClient.describeConsumerGroups(groupIds);
                consumerGroups.addAll(
                    describeConsumerGroupsResult.all().get().values()
                        .stream()
                        .filter(group -> !group.members().isEmpty())
                        .flatMap(group -> group.members().stream())
                        .map(member -> member.consumerId())
                        .collect(Collectors.toSet()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.error("Interrupted when looking for consumer groups: {}", topic, e);
            } catch (ExecutionException e) {
                LOG.error("Failed to list consumer groups for topic: {}", topic, e);
            }
        }
        return consumerGroups;
    }

    @Override
    public void deleteTopic(List<String> consumerGroups, Topic topic)
        throws KafkaDeleteTopicException {
        String topicToDelete = topic.getName();
        Collection<AdminClient> adminClients = kafkaAdminClient.getAll(topic.getClusterType()).values();
        for (AdminClient adminClient : adminClients) {
            try {
                List<String> topicsToDelete = adminClient.listTopics().names().get().stream()
                    .filter(topicName -> topicName.startsWith(topicToDelete))
                    .collect(Collectors.toList());
                if (!topicsToDelete.isEmpty()) {
                    LOG.warn("Deleting topics {}", topicsToDelete);
                    lookForConnectedMembersAtConsumerGroups(consumerGroups, topic);
                    DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(topicsToDelete);
                    deleteTopicsResult.all().get(topicDeletionTimeoutMs, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.error("Interrupted when deleting topic: {}", topicToDelete, e);
            } catch (ExecutionException | TimeoutException e) {
                LOG.error("Failed to delete topic: {}", topicToDelete, e);
            }
        }
    }

    @Override
    public TopicConfig getConfig(Topic topic) {
        TopicConfig topicConfig = topicConfigs.get(topic.getName());
        if (topicConfig == null) {
            List<TopicConfig> newTopicConfigs = getTopicSet(topic).getTopicConfigs();
            for (TopicConfig newTopicConfig : newTopicConfigs) {
                LOG.info("Initialized from defaults config {} for topic {}", newTopicConfig, newTopicConfig.getTopic());
                topicConfigs.put(newTopicConfig.getTopic().getName(), newTopicConfig);
            }
            topicConfig = topicConfigs.get(topic.getName());
        }
        return topicConfig;
    }

    @Override
    public Set<String> retrieveTopics(KafkaClusterType kafkaClusterType, String bootstrapServers) {
        Set<String> topics = ConcurrentHashMap.newKeySet();
        try {
            topics.addAll(kafkaAdminClient.getInstance(kafkaClusterType, bootstrapServers).listTopics().names()
                .get(requestTimeoutMs, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            LOG.error("#FATAL kafka topic service failed to retrieve existing topics for server {}", bootstrapServers,
                e);
        }
        return topics;
    }

    private TopicSet getTopicSet(Topic topic) {
        for (TopicSetDefaulter topicSetDefaulter : topicSetDefaulters) {
            if (topicSetDefaulter.isInterested(topic)) {
                return topicSetDefaulter.create(topic).build();
            }
        }
        return baseTopicSetDefaulter.create(topic).build();
    }

    private void lookForConnectedMembersAtConsumerGroups(List<String> consumerGroups, Topic topic)
        throws KafkaDeleteTopicException {
        Set<String> consumerIds = listConsumers(consumerGroups, topic);
        if (!consumerIds.isEmpty()) {
            String logMessage =
                String.format("There are consumers for topic = %s, can't remove it now, consumerIds=%s",
                    topic.getName(), consumerIds);
            throw new KafkaDeleteTopicException(logMessage);
        }
    }

    private void setTopicRetention(TopicConfig topicConfig) {
        Collection<AlterConfigOp> configs = new ArrayList<>();
        if (topicConfig.getLogRetentionMs().isPresent()) {
            if (topicConfig.getRetryInterval().toMillis() > topicConfig.getLogRetentionMs().get()) {
                LOG.error("Invalid logRetentionMs={} for topic={}, retryInterval={} should be smaller than " +
                    "logRetentionMs", topicConfig.getLogRetentionMs().get(), topicConfig.getRetryInterval(),
                    topicConfig.getTopic());
            } else {
                configs.add(prepTopicLogRetentionMsConfigs(topicConfig.getLogRetentionMs()
                    .get()));
            }
        } else if (topicConfig.getRetryInterval().toMillis() > KAFKA_DEFAULT_RETENTION_PERIOD.toMillis()) {
            LOG.info("Setting logRetentionMs={} for topic={}", topicConfig.getRetryInterval().toMillis(),
                topicConfig.getTopic());
            configs.add(prepTopicLogRetentionMsConfigs(
                KAFKA_RETRY_SAFE_INTERVAL.plusMillis(topicConfig.getRetryInterval().toMillis()).toMillis()));
        }
        if (topicConfig.getPartitionRetentionBytes().isPresent()) {
            configs.add(preparePartitionRetentionBytes(topicConfig.getPartitionRetentionBytes().get()));
        }

        if (!configs.isEmpty()) {
            Collection<AdminClient> adminClients =
                kafkaAdminClient.getAll(topicConfig.getTopic().getClusterType()).values();

            for (AdminClient adminClient : adminClients) {
                executeAlterConfigurationOperations(adminClient, topicConfig.getTopic(), configs);
            }
        }
    }

    private AlterConfigOp prepTopicLogRetentionMsConfigs(long retentionMs) {
        return prepareAlterTopicConfigOperation(
            org.apache.kafka.common.config.TopicConfig.RETENTION_MS_CONFIG, String.valueOf(retentionMs));
    }

    private AlterConfigOp preparePartitionRetentionBytes(long retentionBytes) {
        return prepareAlterTopicConfigOperation(
            org.apache.kafka.common.config.TopicConfig.RETENTION_BYTES_CONFIG, String.valueOf(retentionBytes));
    }

    private static AlterConfigOp prepareAlterTopicConfigOperation(String configName, String configValue) {
        ConfigEntry retentionEntry = new ConfigEntry(configName, configValue);
        AlterConfigOp configOperation = new AlterConfigOp(retentionEntry, AlterConfigOp.OpType.SET);
        return configOperation;
    }

    private static void executeAlterConfigurationOperations(AdminClient adminClient,
        Topic topic, Collection<AlterConfigOp> configs) {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic.getName());
        Map<ConfigResource, Collection<AlterConfigOp>> configMap = new HashMap<>(1);
        configMap.put(resource, configs);
        AlterConfigsResult alterConfigsResult = adminClient.incrementalAlterConfigs(configMap);
        alterConfigsResult.all();
    }
}

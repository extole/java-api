package com.extole.common.event.topic;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.Topic;

public class TopicSetBuilderImpl implements TopicSetBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(TopicSetBuilderImpl.class);
    private static final String RETRY_SUFFIX = "_retry";

    private final String baseName;
    private final KafkaClusterType clusterType;
    private final List<TopicConfigBuilder> topicConfigBuilders = new ArrayList<>();

    public TopicSetBuilderImpl(List<TopicConfig> defaultTopicConfigs, Topic topic) {
        this.baseName = getBaseName(topic.getName());
        this.clusterType = topic.getClusterType();
        int attempt = 0;
        for (TopicConfig defaultConfig : defaultTopicConfigs) {
            topicConfigBuilders.add(new TopicConfigBuilder(defaultConfig)
                .withTopic(new Topic(getTopicName(baseName, attempt), clusterType)));
            attempt++;
        }
    }

    private String getTopicName(String baseName, int attempt) {
        String topicName;
        if (attempt < 1) {
            topicName = baseName;
        } else {
            topicName = baseName + RETRY_SUFFIX + (attempt);
        }
        return topicName;
    }

    private String getBaseName(String topicName) {
        if (topicName.endsWith(TopicService.DEAD_SUFFIX)) {
            return topicName.substring(0, topicName.length() - TopicService.DEAD_SUFFIX.length());
        }
        if (topicName.contains(RETRY_SUFFIX)) {
            return topicName.substring(0, topicName.length() - RETRY_SUFFIX.length() - 1);
        }
        return topicName;
    }

    @Override
    public TopicConfigBuilder editAttempt(int attempt) {
        return topicConfigBuilders.get(attempt);
    }

    @Override
    public TopicSetBuilder setAttemptCount(int attemptCount) {
        LOG.info("setting attempt count to {}. current size {}", attemptCount, topicConfigBuilders.size());
        while (topicConfigBuilders.size() < attemptCount) {
            addAttempt();
        }
        while (topicConfigBuilders.size() > attemptCount) {
            topicConfigBuilders.remove(topicConfigBuilders.size() - 1);
        }
        return this;
    }

    @Override
    public int getAttemptCount() {
        return topicConfigBuilders.size();
    }

    @Override
    public TopicSet build() {
        Topic onFailureTopic = new Topic(baseName + TopicService.DEAD_SUFFIX, clusterType);
        addAttempt().withTopic(onFailureTopic);

        List<TopicConfig> topicConfigs = new ArrayList<>();
        for (TopicConfigBuilder topicConfigBuilder : Lists.reverse(topicConfigBuilders)) {
            LOG.info("topic config builder {} on failure topic {}", topicConfigBuilder, onFailureTopic);
            TopicConfig topicConfig = topicConfigBuilder.withOnFailureTopic(onFailureTopic).build();
            topicConfigs.add(topicConfig);
            onFailureTopic = topicConfig.getTopic();
        }
        return new TopicSet(topicConfigs);
    }

    private TopicConfigBuilder addAttempt() {
        TopicConfigBuilder lastTopicConfigBuilder = topicConfigBuilders.get(topicConfigBuilders.size() - 1);
        TopicConfigBuilder newTopicConfigBuilder = new TopicConfigBuilder(lastTopicConfigBuilder.build())
            .withTopic(new Topic(getTopicName(baseName, topicConfigBuilders.size()), clusterType));
        topicConfigBuilders.add(newTopicConfigBuilder);
        return newTopicConfigBuilder;
    }

}

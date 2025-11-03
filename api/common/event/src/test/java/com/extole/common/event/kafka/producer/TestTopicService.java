package com.extole.common.event.kafka.producer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.Topic;
import com.extole.common.event.topic.TopicConfig;
import com.extole.common.event.topic.TopicConfigBuilder;
import com.extole.common.event.topic.TopicConfigImpl;
import com.extole.common.event.topic.TopicService;

@Component
public class TestTopicService implements TopicService {
    private final Map<String, TopicConfig> topicConfigs = new ConcurrentHashMap<>();

    @Override
    public void validateTopic(Topic topic, String bootstrapServers) {
        // Do Nothing
    }

    @Override
    public void deleteTopic(List<String> consumerGroups, Topic topic) {
        // Do Nothing
    }

    public void setConfig(Topic topic, Consumer<TopicConfigBuilder> topicConfigEditor) {
        TopicConfigBuilder builder = newTopicConfig(topic);
        topicConfigEditor.accept(builder);
        topicConfigs.put(topic.getName(), builder.build());
    }

    @Override
    public TopicConfig getConfig(Topic topic) {
        TopicConfig testConfig = topicConfigs.get(topic.getName());
        return testConfig != null ? testConfig
            : newTopicConfig(topic).build();
    }

    @Override
    public Set<String> retrieveTopics(KafkaClusterType kafkaClusterType, String bootstrapServers) {
        return topicConfigs.values().stream()
            .map(TopicConfig::getTopic)
            .filter(topic -> topic.getClusterType() == kafkaClusterType)
            .map(Topic::getName)
            .collect(Collectors.toSet());
    }

    private TopicConfigBuilder newTopicConfig(Topic topic) {
        return new TopicConfigBuilder(new TopicConfigImpl(0,
            topic,
            Duration.ZERO,
            1L,
            new Topic(topic.getName() + TopicService.DEAD_SUFFIX, topic.getClusterType()),
            Integer.valueOf(1),
            Short.valueOf((short) 1), Optional.empty(), Optional.empty()));
    }
}

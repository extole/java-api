package com.extole.common.event.topic;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.KafkaDeleteTopicException;
import com.extole.common.event.Topic;
import com.extole.spring.StartFirstStopLast;

public interface TopicService extends StartFirstStopLast {
    String DEAD_SUFFIX = "_dead";

    void validateTopic(Topic topic, String bootstrapServers) throws InterruptedException, ExecutionException;

    void deleteTopic(List<String> consumerGroups, Topic topic) throws KafkaDeleteTopicException;

    TopicConfig getConfig(Topic topic);

    Set<String> retrieveTopics(KafkaClusterType kafkaClusterType, String bootstrapServers);
}

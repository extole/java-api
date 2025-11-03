package com.extole.common.event.topic;

import java.util.List;

import com.extole.common.lang.ToString;

public class TopicSet {
    private final List<TopicConfig> topicConfigs;

    public TopicSet(List<TopicConfig> topicConfigs) {
        this.topicConfigs = topicConfigs;
    }

    public List<TopicConfig> getTopicConfigs() {
        return topicConfigs;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

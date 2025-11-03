package com.extole.common.event.recent;

import com.extole.common.lang.ToString;

public class TopicSummary {
    private final String topic;
    private final long processedEvents;
    private final long totalEvents;

    public TopicSummary(String topic, long processedEvents, long totalEvents) {
        this.topic = topic;
        this.processedEvents = processedEvents;
        this.totalEvents = totalEvents;
    }

    public String getTopic() {
        return topic;
    }

    public long getProcessedEvents() {
        return processedEvents;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

package com.extole.common.event.kafka;

import com.extole.common.event.BatchEventListener;
import com.extole.common.event.EventListener;
import com.extole.common.event.Topic;

public interface ExtoleKafkaConsumer<T> {

    String getGroupId();

    void addTopic(Topic topic);

    void removeTopic(Topic topic);

    void startup(EventListener<T> eventListener);

    void startup(BatchEventListener<T> batchEventListener);

    void shutdown();
}

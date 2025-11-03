package com.extole.common.event.recent;

import com.extole.common.event.CloseableIterator;
import com.extole.common.event.Topic;
import com.extole.common.event.migration.EventMigrationService;
import com.extole.id.Id;

public interface RecentEventService {

    <E> RecentEventQueryBuilder<E> createRecentEventQuery(Topic topic, Class<E> classType, Id<?> sourceId);

    TopicSummary getTopicSummary(Topic topic, String groupId) throws KafkaFetchException, KafkaTopicNotFoundException;

    interface RecentEventQueryBuilder<T> {

        RecentEventQueryBuilder<T> withEventMigrationService(EventMigrationService eventMigrationService);

        CloseableIterator<T> query();

    }
}

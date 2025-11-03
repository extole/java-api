package com.extole.common.event.kafka.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.extole.common.event.BatchEventListener;
import com.extole.common.event.EventListener;
import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.ExtoleKafkaConsumer;
import com.extole.common.event.kafka.KafkaTopicMetadata;
import com.extole.common.log.PerformanceLogger;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaEventConsumerPerformanceLogsTest {
    private final MockConsumer<String, String> consumer = new MockConsumer<>(OffsetResetStrategy.LATEST);

    @Test
    public void testConsumerReadMessageFromKafka() {
        Topic topic = new Topic("1", KafkaClusterType.EDGE);
        Topic onFailureTopic = new Topic("onFailure", KafkaClusterType.EDGE);
        TopicPartition topicPartition = new TopicPartition(topic.getName(), 0);
        consumer.updateEndOffsets(Collections.singletonMap(topicPartition, 0L));
        consumer.assign(Collections.singleton(topicPartition));
        consumer.addRecord(new ConsumerRecord<>(topic.getName(), 0, 0, "key", "value"));

        PerformanceLogger performanceLogger = new PerformanceLogger();
        PerformanceLogsEventListener performanceLogsEventListener = new PerformanceLogsEventListener();
        PerformanceLogsKafkaConsumer<String> eventConsumer =
            new PerformanceLogsKafkaConsumer<>(consumer, topic, onFailureTopic, performanceLogger);
        eventConsumer.startup(performanceLogsEventListener);

        await().untilAsserted(() -> {
            List<String> messagesWithDurationAbove = performanceLogger.getMessagesWithDurationAbove(50L);
            assertThat(messagesWithDurationAbove).hasSize(1);
        });
    }

    private static final class PerformanceLogsKafkaConsumer<T> implements ExtoleKafkaConsumer<T> {
        private final Consumer<String, T> consumer;
        private final Topic topic;
        private final Topic onFailureTopic;
        private final PerformanceLogger performanceLogger;

        private PerformanceLogsKafkaConsumer(Consumer<String, T> consumer,
            Topic topic, Topic onFailureTopic, PerformanceLogger performanceLogger) {
            this.consumer = consumer;
            this.topic = topic;
            this.onFailureTopic = onFailureTopic;
            this.performanceLogger = performanceLogger;
        }

        @Override
        public String getGroupId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addTopic(Topic topic) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeTopic(Topic topic) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void startup(EventListener<T> eventListener) {
            ConsumerRecords<String, T> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
            Set<TopicPartition> partitions = records.partitions();
            for (ConsumerRecord<String, T> record : records) {
                try {
                    eventListener.handleEvent(record.value(), new KafkaTopicMetadata(0,
                        topic, 0, 0, onFailureTopic, performanceLogger));
                } catch (Exception e) {
                    throw new RuntimeException("Test KafkaConsumer failed", e);
                }
            }
        }

        @Override
        public void startup(BatchEventListener<T> batchEventListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class PerformanceLogsEventListener implements EventListener {

        @Override
        public void handleEvent(Object event, KafkaTopicMetadata topicMetadata) {
            topicMetadata.getPerformanceLogger().log("test message", Duration.ofMillis(100).toMillis());
            topicMetadata.getPerformanceLogger().log("fast log message", Duration.ofMillis(10).toMillis());
        }

        @Override
        public void afterSingletonsInstantiated() {
        }
    }
}

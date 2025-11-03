package com.extole.common.event.kafka.consumer;

import static com.extole.common.event.kafka.KafkaHeaders.HEADER_PROCESS_AFTER;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.event.kafka.serializer.exception.DeserializerRuntimeException;
import com.extole.common.lang.ToString;
import com.extole.common.lang.date.ExtoleTimeModule;

// TODO split PartitionContext into two different PartitionProcessors ENG-12781
class PartitionContext {
    private static final Logger LOG = LoggerFactory.getLogger(PartitionContext.class);
    private static final long RETRY_BACKOFF_MS = 10L;
    private static final ObjectMapper OBJECT_MAPPER =
        new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new ExtoleTimeModule())
            .registerModule(new Jdk8Module())
            .registerModule(new GuavaModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final TopicPartition topicPartition;
    private final AtomicBoolean isDone;
    private final AtomicLong lastOffsetProcessed;
    private final AtomicLong lastOffsetCommitted;
    private final LinkedBlockingQueue<ConsumerRecord<String, String>> recordsToProcess;
    private final AtomicBoolean isRevoked;
    @Nullable
    private ConsumerRecord<String, String> nextRecord;

    PartitionContext(TopicPartition topicPartition, long initialOffset) {
        this.topicPartition = topicPartition;
        this.recordsToProcess = new LinkedBlockingQueue<>();
        this.isDone = new AtomicBoolean(false);
        this.lastOffsetProcessed = new AtomicLong(initialOffset - 1);
        this.lastOffsetCommitted = new AtomicLong(initialOffset);
        this.isRevoked = new AtomicBoolean(false);
    }

    TopicPartition getTopicPartition() {
        return topicPartition;
    }

    int getRecordCount() {
        return recordsToProcess.size();
    }

    boolean isDone() {
        return isDone.get();
    }

    void markAsDone() {
        this.isDone.set(true);
    }

    long getLastProcessedOffset() {
        return lastOffsetProcessed.get();
    }

    void setLastProcessedOffset(long offset) {
        this.lastOffsetProcessed.set(offset);
    }

    boolean isRevoked() {
        return isRevoked.get();
    }

    void revoke() {
        this.isRevoked.set(true);
    }

    void setLastOffsetCommitted(long offset) {
        this.lastOffsetCommitted.set(offset);
    }

    Optional<OffsetAndMetadata> getOffsetToCommit() {
        if (lastOffsetProcessed.get() >= lastOffsetCommitted.get()) {
            return Optional.of(new OffsetAndMetadata(lastOffsetProcessed.get() + 1));
        }
        return Optional.empty();
    }

    Optional<ConsumerRecord<String, String>> getNextRecord() throws InterruptedException {
        ConsumerRecord<String, String> record =
            nextRecord != null ? nextRecord : recordsToProcess.poll(RETRY_BACKOFF_MS, TimeUnit.MILLISECONDS);
        if (record != null && allowedToProcessNow(record)) {
            nextRecord = null;
            return Optional.of(record);
        } else {
            nextRecord = record;
            return Optional.empty();
        }
    }

    void addRecords(List<ConsumerRecord<String, String>> records) {
        recordsToProcess.addAll(records);
    }

    private boolean allowedToProcessNow(ConsumerRecord<String, String> record) throws InterruptedException {
        Header processAfterHeader = record.headers().lastHeader(HEADER_PROCESS_AFTER);
        if (processAfterHeader != null) {
            try {
                Instant processAfter = OBJECT_MAPPER.readValue(processAfterHeader.value(), Instant.class);
                if (processAfter.isAfter(Instant.now())) {
                    LOG.trace("{} detected record with processAfter {}. Sleeping {}ms before trying again.",
                        topicPartition, processAfter, String.valueOf(RETRY_BACKOFF_MS));
                    Thread.sleep(RETRY_BACKOFF_MS);
                    return false;
                }
            } catch (IOException e) {
                throw new DeserializerRuntimeException("Failed to decode instant", e);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

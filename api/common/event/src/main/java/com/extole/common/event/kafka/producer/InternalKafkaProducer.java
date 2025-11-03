package com.extole.common.event.kafka.producer;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import io.opentelemetry.instrumentation.kafkaclients.v2_6.KafkaTelemetry;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.Topic;
import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.topic.TopicService;
import com.extole.common.metrics.ExtoleHistogram;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.telemetry.ExtoleOpenTelemetryFactory;

class InternalKafkaProducer {
    private static final Logger LOG = LoggerFactory.getLogger(InternalKafkaProducer.class);
    private static final String HISTOGRAM_POSTFIX = ".duration";
    private static final int MAX_LOG_EVENT_LENGTH = 100;

    private final Producer<String, String> producer;
    private final KafkaClusterType clusterType;
    private final ExtoleMetricRegistry metricRegistry;
    private final KafkaProducerConfig kafkaProducerConfig;
    private final AtomicBoolean shutdown = new AtomicBoolean();
    private final String parentProducerClassName;

    InternalKafkaProducer(KafkaClusterType clusterType,
        KafkaProducerConfig kafkaProducerConfig,
        ExtoleMetricRegistry metricRegistry,
        Class<?> parentProducerClass,
        String producerIdPrefix) {
        KafkaTelemetry telemetry = KafkaTelemetry.create(ExtoleOpenTelemetryFactory.globalInstance());
        this.producer = telemetry.wrap(
            kafkaProducerConfig.constructProducer(producerIdPrefix + "-" + UUID.randomUUID(), clusterType));
        this.clusterType = clusterType;
        this.kafkaProducerConfig = kafkaProducerConfig;
        this.metricRegistry = metricRegistry;
        this.parentProducerClassName = parentProducerClass.getName();
    }

    Future<RecordMetadata> send(ProducerRecord<String, String> producerRecord, Topic topic) {
        if (clusterType != topic.getClusterType()) {
            throw new KafkaEventProducerRuntimeException(
                String.format("Attempted to produce to the wrong cluster - event of type %s "
                    + " to topic %s", StringUtils.truncate(producerRecord.value(), MAX_LOG_EVENT_LENGTH),
                    topic.getName()));
        }
        Instant startSendTime = Instant.now();
        return producer.send(producerRecord, (metadata, producerException) -> {
            if (producerException == null) {
                getSuccessHistogram(topic).update(startSendTime, Instant.now());
            } else {
                LOG.error("#FATAL[https://extole.atlassian.net/wiki/display/ENG/RUNBOOK+Kafka#RUNBOOK"
                    + "Kafka-JumpingtoConclusions-SickBroker] failed to produce to " + topic.getName()
                    + " event: " + StringUtils.truncate(producerRecord.value(), MAX_LOG_EVENT_LENGTH)
                    + " - sick broker warning", producerException);
                getErrorHistogram(topic).update(startSendTime, Instant.now());
            }
        });
    }

    String getBootstrapServers() {
        return kafkaProducerConfig.getBootstrapServers(clusterType);
    }

    void shutdown() {
        if (!shutdown.getAndSet(true)) {
            producer.flush();
            producer.close();
        } else {
            LOG.warn("Producer for cluster {} has already shut down", clusterType);
        }
    }

    void checkDataLength(ProducerRecord<String, String> record, Topic topic) throws EventTooLargeException {
        int maxRequestSizeBytes = isRetryTopic(topic) ? kafkaProducerConfig.getMaxRetryRequestSizeBytes().intValue()
            : kafkaProducerConfig.getMaxRequestSizeBytes().intValue();
        int recordLength = record.toString().getBytes().length;
        if (recordLength > maxRequestSizeBytes) {
            throw new EventTooLargeException("Event being sent to topic " + topic.getName()
                + " exceeds size limit of " + maxRequestSizeBytes + " bytes", recordLength, maxRequestSizeBytes);

        }
        getBytesHistogram(topic).update(recordLength);
    }

    private boolean isRetryTopic(Topic topic) {
        return topic.getName().endsWith(TopicService.DEAD_SUFFIX)
            || topic.getName().substring(0, topic.getName().length() - 1).endsWith("_retry");
    }

    private ExtoleHistogram getErrorHistogram(Topic topic) {
        return metricRegistry
            .histogram(parentProducerClassName + "." + topic.getName() + HISTOGRAM_POSTFIX + ".error");
    }

    private ExtoleHistogram getSuccessHistogram(Topic topic) {
        return metricRegistry
            .histogram(parentProducerClassName + "." + topic.getName() + HISTOGRAM_POSTFIX + ".success");
    }

    private ExtoleHistogram getBytesHistogram(Topic topic) {
        return metricRegistry
            .histogram(parentProducerClassName + "." + topic.getName() + HISTOGRAM_POSTFIX + ".bytes");
    }
}

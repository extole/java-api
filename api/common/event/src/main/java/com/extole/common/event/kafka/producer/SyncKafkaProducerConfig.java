package com.extole.common.event.kafka.producer;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.event.KafkaClusterType;
import com.extole.common.lang.ToString;

@Component
public class SyncKafkaProducerConfig implements KafkaProducerConfig, Serializable {

    private final String bootstrapServers;
    private final String globalBootstrapServers;
    private final String serializerClass;
    private final Integer maxInFlightRequestsPerConnection;
    private final Long retries;
    private final Long metadataMaxAgeMs;
    private final Long batchSize;
    private final Long lingerMs;
    private final String compressionType;
    private final Integer maxRequestSizeBytes;
    private final Integer maxRetryRequestSizeBytes;
    private final Boolean enableIdempotence;
    private final Integer requestTimeoutMs;
    private final Long maxBlockMs;
    private final String acknowledgementPolicy;
    private final Long bufferMemory;
    private final Integer deliveryTimeoutMs;

    @Autowired
    public SyncKafkaProducerConfig(
        @Value("${kafka.producer.bootstrap.servers:kafka-${aws.availability.zone:}-private.${extole.environment:lo}"
            + ".intole.net:9092}") String bootstrapServers,
        @Value("${kafka.global.producer.bootstrap.servers:kafka-private.${extole.environment:lo}"
            + ".intole.net:9092}") String globalBootstrapServers,
        @Value("${kafka.key.serializer:org.apache.kafka.common.serialization.StringSerializer}") String serializerClass,
        @Value("${kafka.max.in.flight.request.per.connection:5}") Integer maxInFlightRequestsPerConnection,
        @Value("${kafka.producer.retries:100}") Long retries,
        @Value("${kafka.producer.metadata.max.age.ms:15000}") Long metadataMaxAgeMs,
        @Value("${kafka.producer.batch.size:16384}") Long batchSize,
        @Value("${kafka.producer.linger.ms:5}") Long lingerMs,
        @Value("${kafka.producer.compression.type:snappy}") String compressionType,
        @Value("${kafka.producer.max.request.size.bytes:8000000}") Integer maxRequestSizeBytes,
        @Value("${kafka.producer.max.retry.request.size.bytes:8000000}") Integer maxRetryRequestSizeBytes,
        @Value("${kafka.producer.enable.idempotence:false}") Boolean enableIdempotence,
        @Value("${kafka.sync.producer.request.timeout.ms:2000}") Integer requestTimeoutMs,
        @Value("${kafka.sync.producer.max.block.ms:5000}") Long maxBlockMs,
        @Value("${kafka.sync.producer.acks:all}") String acknowledgementPolicy,
        @Value("${kafka.sync.producer.buffer.memory.bytes:100000000}") Long bufferMemory,
        @Value("${kafka.sync.producer.delivery.timeout.ms:10000}") Integer deliveryTimeoutMs) {
        this.bootstrapServers = bootstrapServers;
        this.globalBootstrapServers = globalBootstrapServers;
        this.serializerClass = serializerClass;
        this.maxInFlightRequestsPerConnection = maxInFlightRequestsPerConnection;
        this.retries = retries;
        this.metadataMaxAgeMs = metadataMaxAgeMs;
        this.batchSize = batchSize;
        this.lingerMs = lingerMs;
        this.compressionType = compressionType;
        this.maxRequestSizeBytes = maxRequestSizeBytes;
        this.maxRetryRequestSizeBytes = maxRetryRequestSizeBytes;
        this.requestTimeoutMs = requestTimeoutMs;
        this.maxBlockMs = maxBlockMs;
        this.acknowledgementPolicy = acknowledgementPolicy;
        this.enableIdempotence = enableIdempotence;
        this.bufferMemory = bufferMemory;
        this.deliveryTimeoutMs = deliveryTimeoutMs;
    }

    @Override
    public String getSerializerClass() {
        return serializerClass;
    }

    @Override
    public String getBootstrapServers(KafkaClusterType kafkaClusterType) {
        return KafkaClusterType.EDGE == kafkaClusterType ? bootstrapServers : globalBootstrapServers;
    }

    @Override
    public Integer getMaxInFlightRequestsPerConnection() {
        return maxInFlightRequestsPerConnection;
    }

    @Override
    public Long getRetries() {
        return retries;
    }

    @Override
    public Long getMetadataMaxAgeMs() {
        return metadataMaxAgeMs;
    }

    @Override
    public Long getBatchSize() {
        return batchSize;
    }

    @Override
    public Long getLingerMs() {
        return lingerMs;
    }

    @Override
    public String getCompressionType() {
        return compressionType;
    }

    @Override
    public Integer getMaxRetryRequestSizeBytes() {
        return maxRetryRequestSizeBytes;
    }

    @Override
    public Integer getMaxRequestSizeBytes() {
        return maxRequestSizeBytes;
    }

    @Override
    public Boolean enableIdempotence() {
        return enableIdempotence;
    }

    @Override
    public Integer getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    @Override
    public Long getMaxBlockMs() {
        return maxBlockMs;
    }

    @Override
    public String getAcknowledgementPolicy() {
        return acknowledgementPolicy;
    }

    @Override
    public Long getBufferMemory() {
        return bufferMemory;
    }

    @Override
    public Integer getDeliveryTimeoutMs() {
        return deliveryTimeoutMs;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

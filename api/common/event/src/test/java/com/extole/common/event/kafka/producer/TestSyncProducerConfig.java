package com.extole.common.event.kafka.producer;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.extole.common.event.KafkaClusterType;

@Primary
@Component
public class TestSyncProducerConfig extends SyncKafkaProducerConfig {

    private final MockProducer<String, String> edgeSyncProducer;
    private final MockProducer<String, String> globalSyncProducer;

    @Autowired
    public TestSyncProducerConfig(
        @Value("${kafka.producer.bootstrap.servers:kafka-${aws.availability.zone:}-private.${extole.environment:lo}"
            + ".intole.net:9092}") String bootstrapServers,
        @Value("${kafka.global.producer.bootstrap.servers:kafka-private.${extole.environment:lo}"
            + ".intole.net:9092}") String globalBootstrapServers,
        @Value("${kafka.key.serializer:org.apache.kafka.common.serialization.StringSerializer}") String serializerClass,
        @Value("${kafka.max.in.flight.request.per.connection:5}") Integer maxInFlightRequestsPerConnection,
        @Value("${kafka.producer.retries:100}") Long retries,
        @Value("${kafka.producer.metadata.max.age.ms:300000}") Long metadataMaxAgeMs,
        @Value("${kafka.producer.batch.size:16384}") Long batchSize,
        @Value("${kafka.producer.linger.ms:5}") Long lingerMs,
        @Value("${kafka.producer.compression.type:snappy}") String compressionType,
        @Value("${kafka.producer.max.request.size.bytes:8000000}") Integer maxRequestSizeBytes,
        @Value("${kafka.producer.max.retry.request.size.bytes:8000000}") Integer maxRetryRequestSizeBytes,
        @Value("${kafka.producer.enable.idempotence:false}") Boolean enableIdempotence,
        @Value("${kafka.sync.producer.request.timeout.ms:1000}") Integer requestTimeoutMs,
        @Value("${kafka.sync.producer.max.block.ms:5000}") Long maxBlockMs,
        @Value("${kafka.sync.producer.acks:all}") String acknowledgementPolicy,
        @Value("${kafka.sync.producer.buffer.memory.bytes:500000000}") Long bufferMemory,
        @Value("${kafka.sync.producer.delivery.timeout.ms:5000}") Integer deliveryTimeoutMs,
        MockProducer<String, String> edgeSyncProducer,
        MockProducer<String, String> globalSyncProducer) {
        super(bootstrapServers, globalBootstrapServers, serializerClass, maxInFlightRequestsPerConnection, retries,
            metadataMaxAgeMs, batchSize, lingerMs, compressionType, maxRequestSizeBytes, maxRetryRequestSizeBytes,
            enableIdempotence, requestTimeoutMs, maxBlockMs, acknowledgementPolicy, bufferMemory, deliveryTimeoutMs);
        this.edgeSyncProducer = edgeSyncProducer;
        this.globalSyncProducer = globalSyncProducer;
    }

    @Override
    public Producer<String, String> constructProducer(String clientId, KafkaClusterType kafkaClusterType) {
        if (kafkaClusterType.equals(KafkaClusterType.EDGE)) {
            return edgeSyncProducer;
        } else {
            return globalSyncProducer;
        }
    }
}

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
public class TestAsyncProducerConfig extends AsyncKafkaProducerConfig {

    private final MockProducer<String, String> edgeAsyncProducer;
    private final MockProducer<String, String> globalAsyncProducer;

    @Autowired
    public TestAsyncProducerConfig(
        @Value("${kafka.producer.bootstrap.servers:kafka-${aws.availability.zone:}-private.${extole.environment:lo}"
            + ".intole.net:9092}") String bootstrapServers,
        @Value("${kafka.global.producer.bootstrap.servers:kafka-private.${extole.environment:lo}"
            + ".intole.net:9092}") String globalBootstrapServers,
        @Value("${kafka.key.serializer:org.apache.kafka.common.serialization.StringSerializer}") String serializerClass,
        @Value("${kafka.max.in.flight.request.per.connection:5}") Integer maxInFlightRequestsPerConnection,
        @Value("${kafka.producer.retries:100}") Long retries,
        @Value("${kafka.producer.metadata.max.age.ms:300000}") Long metadataMaxAgeMs,
        @Value("${kafka.producer.batch.size:16384}") Long batchSize,
        @Value("${kafka.producer.linger.ms:10}") Long lingerMs,
        @Value("${kafka.producer.compression.type:snappy}") String compressionType,
        @Value("${kafka.producer.max.request.size.bytes:8000000}") Integer maxRequestSizeBytes,
        @Value("${kafka.producer.max.retry.request.size.bytes:8000000}") Integer maxRetryRequestSizeBytes,
        @Value("${kafka.producer.enable.idempotence:false}") Boolean enableIdempotence,
        @Value("${kafka.async.producer.request.timeout.ms:10000}") Integer requestTimeoutMs,
        @Value("${kafka.async.producer.max.block.ms:10000}") Long maxBlockMs,
        @Value("${kafka.async.producer.acks:1}") String acknowledgementPolicy,
        @Value("${kafka.async.producer.buffer.memory.bytes:1000000000}") Long bufferMemory,
        @Value("${kafka.async.producer.delivery.timeout.ms:300000}") Integer deliveryTimeoutMs,
        MockProducer<String, String> edgeAsyncProducer,
        MockProducer<String, String> globalAsyncProducer) {
        super(bootstrapServers, globalBootstrapServers, serializerClass, maxInFlightRequestsPerConnection, retries,
            metadataMaxAgeMs, batchSize, lingerMs, compressionType, maxRequestSizeBytes, maxRetryRequestSizeBytes,
            enableIdempotence, requestTimeoutMs, maxBlockMs, acknowledgementPolicy, bufferMemory, deliveryTimeoutMs);
        this.edgeAsyncProducer = edgeAsyncProducer;
        this.globalAsyncProducer = globalAsyncProducer;
    }

    @Override
    public Producer<String, String> constructProducer(String clientId, KafkaClusterType kafkaClusterType) {
        if (kafkaClusterType.equals(KafkaClusterType.EDGE)) {
            return edgeAsyncProducer;
        } else {
            return globalAsyncProducer;
        }
    }
}

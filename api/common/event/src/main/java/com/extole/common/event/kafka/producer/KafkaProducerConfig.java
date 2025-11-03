package com.extole.common.event.kafka.producer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;

import com.extole.common.event.KafkaClusterType;

interface KafkaProducerConfig {
    String getSerializerClass();

    String getBootstrapServers(KafkaClusterType kafkaClusterType);

    Integer getMaxInFlightRequestsPerConnection();

    Long getRetries();

    Long getMetadataMaxAgeMs();

    Long getBatchSize();

    Long getLingerMs();

    String getCompressionType();

    Integer getMaxRetryRequestSizeBytes();

    Integer getMaxRequestSizeBytes();

    String getAcknowledgementPolicy();

    Integer getRequestTimeoutMs();

    Long getMaxBlockMs();

    Boolean enableIdempotence();

    Long getBufferMemory();

    Integer getDeliveryTimeoutMs();

    default Producer<String, String> constructProducer(String clientId, KafkaClusterType kafkaClusterType) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, getSerializerClass());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, getSerializerClass());
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers(kafkaClusterType));
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
            getMaxInFlightRequestsPerConnection().toString());
        properties.put(ProducerConfig.RETRIES_CONFIG, getRetries().toString());
        properties.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, getMetadataMaxAgeMs().toString());
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, getBatchSize().toString());
        properties.put(ProducerConfig.LINGER_MS_CONFIG, getLingerMs().toString());
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, getCompressionType());
        properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, getMaxRetryRequestSizeBytes());
        properties.put(ProducerConfig.ACKS_CONFIG, getAcknowledgementPolicy());
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, getRequestTimeoutMs());
        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, getMaxBlockMs().toString());
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence());
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, getBufferMemory());
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, getDeliveryTimeoutMs());
        return new KafkaProducer<>(properties);
    }
}

package com.extole.common.event.kafka.producer;

import com.codahale.metrics.MetricRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.extole.common.event.kafka.consumer.KafkaConsumerRecordProcessor;
import com.extole.common.metrics.ExtoleMetricRegistry;

@Configuration
public class KafkaTestConfig {

    @Bean
    public ExtoleMetricRegistry metricRegistry() {
        return new ExtoleMetricRegistry(new MetricRegistry());
    }

    @Bean
    public MockProducer<String, String> edgeSyncProducer() {
        return new MockProducer<>(false, new StringSerializer(), new StringSerializer());
    }

    @Bean
    public MockProducer<String, String> edgeAsyncProducer() {
        return new MockProducer<>(false, new StringSerializer(), new StringSerializer());
    }

    @Bean
    public MockProducer<String, String> globalSyncProducer() {
        return new MockProducer<>(false, new StringSerializer(), new StringSerializer());
    }

    @Bean
    public MockProducer<String, String> globalAsyncProducer() {
        return new MockProducer<>(false, new StringSerializer(), new StringSerializer());
    }

    @Bean
    public OpenTelemetry openTelemetry() {
        return AutoConfiguredOpenTelemetrySdk.builder().build().getOpenTelemetrySdk();
    }

    @Bean
    public KafkaConsumerRecordProcessor kafkaConsumerRecordProcessor(OpenTelemetry openTelemetry) {
        return new KafkaConsumerRecordProcessor(openTelemetry);
    }
}

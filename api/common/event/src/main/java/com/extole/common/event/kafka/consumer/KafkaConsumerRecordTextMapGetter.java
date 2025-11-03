package com.extole.common.event.kafka.consumer;

import java.util.List;

import javax.annotation.Nullable;

import io.opentelemetry.context.propagation.TextMapGetter;
import org.apache.kafka.clients.consumer.ConsumerRecord;

final class KafkaConsumerRecordTextMapGetter implements TextMapGetter<ConsumerRecord<String, String>> {

    private static final String TRACEPARENT_HEADER_NAME = "traceparent";

    @Override
    public Iterable<String> keys(ConsumerRecord<String, String> carrier) {
        return carrier.headers().lastHeader(TRACEPARENT_HEADER_NAME) == null
            ? List.of()
            : List.of(TRACEPARENT_HEADER_NAME);
    }

    @Nullable
    @Override
    public String get(@Nullable ConsumerRecord<String, String> carrier, String key) {
        if (carrier == null || carrier.headers().lastHeader(key) == null) {
            return null;
        }
        return new String(carrier.headers().lastHeader(key).value());
    }
}

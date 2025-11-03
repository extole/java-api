package com.extole.common.event.kafka.consumer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.common.event.BatchEventListener;
import com.extole.common.event.EventListener;
import com.extole.common.event.KafkaRetryException;
import com.extole.common.event.KafkaWaitException;
import com.extole.common.event.kafka.KafkaTopicMetadata;
import com.extole.common.event.kafka.serializer.json.JsonDecoder;
import com.extole.common.event.topic.TopicConfig;

@Component
public class KafkaConsumerRecordProcessor {
    private final KafkaConsumerRecordTextMapGetter kafkaRecordTextMapGetter = new KafkaConsumerRecordTextMapGetter();
    private final OpenTelemetry openTelemetry;
    private final Tracer tracer;

    @Autowired
    public KafkaConsumerRecordProcessor(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
        this.tracer = openTelemetry.getTracer(this.getClass().getSimpleName());
    }

    public <T> void handleEvent(EventListener<T> listener,
        JsonDecoder<T> decoder,
        ConsumerRecord<String, String> record,
        KafkaTopicMetadata metadata) throws KafkaWaitException, KafkaRetryException {
        T event = decoder.decode(record.value().getBytes());
        Context context = openTelemetry
            .getPropagators()
            .getTextMapPropagator()
            .extract(Context.current(), record, kafkaRecordTextMapGetter);

        try (Scope ignored = context.makeCurrent()) {
            Span span = tracer.spanBuilder("handleEvent_" + metadata.getTopic().getName()).startSpan();
            try {
                listener.handleEvent(event, metadata);
            } catch (Throwable t) {
                span.recordException(t);
                throw t;
            } finally {
                span.end();
            }
        }
    }

    public <T> void handleEvents(LinkedList<ConsumerRecord<String, String>> records,
        JsonDecoder<T> decoder,
        BatchEventListener<T> listener,
        KafkaTopicMetadata metadata,
        TopicConfig topicConfig) throws KafkaRetryException {
        SpanBuilder spanBuilder = tracer.spanBuilder("handleEvent_" + metadata.getTopic().getName());
        List<T> eventBatch = new ArrayList<>();
        for (ConsumerRecord<String, String> record : records) {
            T event = decoder.decode(record.value().getBytes());
            eventBatch.add(event);

            Context context = openTelemetry
                .getPropagators()
                .getTextMapPropagator()
                .extract(Context.current(), record, kafkaRecordTextMapGetter);
            spanBuilder.addLink(Span.fromContext(context).getSpanContext());
        }

        Span span = spanBuilder.startSpan();
        try (Scope ignored = span.makeCurrent()) {
            listener.handleEvents(eventBatch, metadata, topicConfig);
        } catch (Throwable t) {
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
}

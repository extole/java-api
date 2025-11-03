package com.extole.common.memcached;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.lang.date.ExtoleTimeModule;
import com.extole.common.metrics.ExtoleMetricRegistry;

@Component
public class JsonMemcachedTransformerFactory {
    private final ExtoleMetricRegistry metricRegistry;
    private final ObjectMapper defaultObjectMapper;
    private final int warningThresholdMs;

    @Autowired
    public JsonMemcachedTransformerFactory(
        @Value("${memcached.serialization.json.warning.threshold.ms:10}") int warningThresholdMs,
        ExtoleMetricRegistry metricRegistry) {
        this.warningThresholdMs = warningThresholdMs;
        this.metricRegistry = metricRegistry;
        this.defaultObjectMapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new ExtoleTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public <T> MemcachedTransformer<String, T> createStringTransformer(String storeName, Class<T> type) {
        return createStringTransformer(storeName, type, defaultObjectMapper);
    }

    public <T> MemcachedTransformer<String, T> createStringTransformer(String storeName, Class<T> type,
        ObjectMapper objectMapper) {
        return new JsonMemcachedTransformer<>(storeName, type, new StringJsonTransformer<>(type, objectMapper),
            new Statistics(warningThresholdMs), metricRegistry);
    }

    public <T> MemcachedTransformer<byte[], T> createBinaryTransformer(String storeName, Class<T> type) {
        return createBinaryTransformer(storeName, type, defaultObjectMapper);
    }

    public <T> MemcachedTransformer<byte[], T> createBinaryTransformer(String storeName, Class<T> type,
        ObjectMapper objectMapper) {
        return new JsonMemcachedTransformer<>(storeName, type, new ByteArrayJsonTransformer<>(type, objectMapper),
            new Statistics(warningThresholdMs), metricRegistry);
    }

    private static final class JsonMemcachedTransformer<S, T> implements MemcachedTransformer<S, T> {
        private static final Logger LOG = LoggerFactory.getLogger(JsonMemcachedTransformer.class);

        private final ExtoleMetricRegistry metricRegistry;
        private final String storeName;
        private final Class<T> type;
        private final Statistics statistics;
        private final JsonTransformer<S, T> transformer;

        private JsonMemcachedTransformer(String storeName, Class<T> type, JsonTransformer<S, T> transformer,
            Statistics statistics, ExtoleMetricRegistry metricRegistry) {
            this.storeName = storeName;
            this.type = type;
            this.transformer = transformer;
            this.statistics = statistics;
            this.metricRegistry = metricRegistry;
        }

        @Override
        public S encode(MemcachedKey key, T value) throws MemcachedTransformerException {
            Instant startTime = Instant.now();
            try {
                S result = transformer.write(value);
                int size = transformer.estimateSize(result);
                metricRegistry.histogram(buildMetricNameWithStore(MemcachedMetric.SERIALIZE_SIZE)).update(size);
                return result;
            } catch (JsonProcessingException e) {
                metricRegistry.counter(buildMetricNameWithStore(MemcachedMetric.SERIALIZE_ERROR_COUNTER)).increment();
                throw new MemcachedTransformerException(
                    "Unable to JSON encode value for key: " + key + " to type: " + type, e);
            } finally {
                long duration = System.currentTimeMillis() - startTime.toEpochMilli();
                statistics.serializeOperation.addTime(duration);
                LOG.trace("Memcached object mapping serialization {}", statistics.serializeOperation);
                metricRegistry.histogram(buildMetricNameWithStore(MemcachedMetric.SERIALIZE_TIME)).update(duration);
            }
        }

        @Override
        public Optional<T> decode(MemcachedKey key, S value) throws MemcachedTransformerException {
            Instant startTime = Instant.now();
            try {
                Optional<T> result = transformer.read(value);
                if (result.isEmpty()) {
                    LOG.warn("Decoded null or empty value for key: {}, type: {} in memcached store: {}", key, type,
                        storeName);
                    return Optional.empty();
                }
                return result;
            } catch (JsonMappingException e) {
                metricRegistry.counter(buildMetricNameWithStore(MemcachedMetric.DESERIALIZE_ERROR_COUNTER)).increment();
                LOG.error("Unable to JSON decode value for key: {} to type: {}. Use Optional.empty().", key, type, e);
                return Optional.empty();
            } catch (IOException e) {
                metricRegistry.counter(buildMetricNameWithStore(MemcachedMetric.DESERIALIZE_ERROR_COUNTER)).increment();
                throw new MemcachedTransformerException(
                    "Unable to JSON decode value for key: " + key + " to type: " + type, e);
            } finally {
                long duration = System.currentTimeMillis() - startTime.toEpochMilli();
                statistics.deserializeOperation.addTime(duration);
                LOG.trace("Memcached object mapping deserialization {}", statistics.deserializeOperation);
                metricRegistry.histogram(buildMetricNameWithStore(MemcachedMetric.DESERIALIZE_TIME)).update(duration);
            }
        }

        private String buildMetricNameWithStore(MemcachedMetric memcachedMetric) {
            return storeName + "." + memcachedMetric.getMetricName();
        }
    }

    private interface JsonTransformer<S, T> {
        S write(T value) throws JsonProcessingException;

        Optional<T> read(S value) throws JsonMappingException, JsonProcessingException, IOException;

        int estimateSize(S value);
    }

    private static final class StringJsonTransformer<T> implements JsonTransformer<String, T> {
        private final Class<T> type;
        private final ObjectMapper objectMapper;

        private StringJsonTransformer(Class<T> type, ObjectMapper objectMapper) {
            this.type = type;
            this.objectMapper = objectMapper;
        }

        @Override
        public String write(T value) throws JsonProcessingException {
            return objectMapper.writeValueAsString(value);
        }

        @Override
        public Optional<T> read(String value) throws JsonMappingException, JsonProcessingException {
            if (Strings.isNullOrEmpty(value)) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(value, type));
        }

        @Override
        public int estimateSize(String value) {
            return value != null ? value.length() : 0;
        }
    }

    private static final class ByteArrayJsonTransformer<T> implements JsonTransformer<byte[], T> {
        private final Class<T> type;
        private final ObjectMapper objectMapper;

        private ByteArrayJsonTransformer(Class<T> type, ObjectMapper objectMapper) {
            this.type = type;
            this.objectMapper = objectMapper;
        }

        @Override
        public byte[] write(T value) throws JsonProcessingException {
            return objectMapper.writeValueAsBytes(value);
        }

        @Override
        public Optional<T> read(byte[] value) throws JsonMappingException, JsonProcessingException, IOException {
            if (value == null || value.length == 0) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(value, type));
        }

        @Override
        public int estimateSize(byte[] value) {
            return value != null ? value.length : 0;
        }
    }

    private static final class Statistics {
        private final OperationStatistics serializeOperation;
        private final OperationStatistics deserializeOperation;

        Statistics(int thresholdMs) {
            serializeOperation = new OperationStatistics(thresholdMs);
            deserializeOperation = new OperationStatistics(thresholdMs);
        }
    }

    private static class OperationStatistics {
        private long totalTime = 0;
        private long operations = 0;
        private long maxTime = 0;
        private long minTime = -1;
        private long warningThresholdMs = 0;
        private int warningEntries = 0;
        private long clearCounter = 0;

        OperationStatistics(long warningThresholdMs) {
            this.warningThresholdMs = warningThresholdMs;
        }

        public synchronized void addTime(long time) {
            if (Long.MAX_VALUE - time <= totalTime || Long.MAX_VALUE - 1 <= operations) {
                clear();
            }

            totalTime += time;
            operations++;
            maxTime = Long.max(maxTime, time);
            if (minTime == -1) {
                minTime = time;
            } else {
                minTime = Long.min(minTime, time);
            }
            if (time > warningThresholdMs) {
                warningEntries++;
            }
        }

        private void clear() {
            if (Long.MAX_VALUE > clearCounter) {
                clearCounter++;
            }
            totalTime = 0;
            operations = 0;
            maxTime = 0;
            minTime = 0;
            warningEntries = 0;
        }

        private long getAverageTime() {
            return operations <= 0 ? 0 : totalTime / operations;
        }

        @Override
        public synchronized String toString() {
            return "OperationStats{" +
                "totalTime=" + totalTime + "ms" +
                ", operations=" + operations +
                ", maxTime=" + maxTime + "ms" +
                ", minTime=" + minTime + "ms" +
                ", avgTime=" + getAverageTime() + "ms" +
                ", warningThreshold=" + warningThresholdMs + "ms" +
                ", warningEntries=" + warningEntries +
                '}';
        }
    }

}

package com.extole.common.memcached;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.extole.common.metrics.ExtoleMetricRegistry;

public class StringMemcachedTransformer implements MemcachedTransformer<byte[], String> {
    private final ExtoleMetricRegistry metricRegistry;
    private final String storeName;

    public StringMemcachedTransformer(ExtoleMetricRegistry metricRegistry, String storeName) {
        this.metricRegistry = metricRegistry;
        this.storeName = storeName;
    }

    @Override
    public byte[] encode(MemcachedKey key, String value) {
        int size = value != null ? value.length() : 0;
        metricRegistry.histogram(buildMetricNameWithStore(MemcachedMetric.SERIALIZE_SIZE)).update(size);
        return value != null ? value.getBytes(StandardCharsets.UTF_8) : null;
    }

    @Override
    public Optional<String> decode(MemcachedKey key, byte[] value) {
        if (value == null || value.length == 0) {
            return Optional.empty();
        }
        return Optional.of(new String(value, StandardCharsets.UTF_8));
    }

    private String buildMetricNameWithStore(MemcachedMetric memcachedMetric) {
        return storeName + "." + memcachedMetric.getMetricName();
    }
}

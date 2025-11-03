package com.extole.common.memcached;

import java.util.Optional;

import com.extole.common.metrics.ExtoleMetricRegistry;

public class NoOpMemcachedTransformer implements MemcachedTransformer<Object, Object> {
    private final ExtoleMetricRegistry metricRegistry;
    private final String storeName;

    public NoOpMemcachedTransformer(ExtoleMetricRegistry metricRegistry, String storeName) {
        this.metricRegistry = metricRegistry;
        this.storeName = storeName;
    }

    @Override
    public Object encode(MemcachedKey key, Object value) {
        int size = value != null ? String.valueOf(value).length() : 0;
        metricRegistry.histogram(buildMetricNameWithStore(MemcachedMetric.SERIALIZE_SIZE)).update(size);
        return value;
    }

    @Override
    public Optional<Object> decode(MemcachedKey key, Object value) {
        return Optional.of(value);
    }

    private String buildMetricNameWithStore(MemcachedMetric memcachedMetric) {
        return storeName + "." + memcachedMetric.getMetricName();
    }
}

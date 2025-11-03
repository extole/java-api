package com.extole.common.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.cache.Cache;

public final class GuavaCacheMetrics implements MetricSet {
    private static final String CACHE_PREFIX = "com.extole.common.metrics.cache.";
    private final HashMap<String, Metric> metrics = new HashMap<>();

    private GuavaCacheMetrics(String cacheName, final Cache<?, ?> cache) {
        metrics.put(name(cacheName, "size"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return Long.valueOf(cache.size());
            }
        });
        metrics.put(name(cacheName, "hitRate"), new Gauge<Double>() {
            @Override
            public Double getValue() {
                return Double.valueOf(cache.stats().hitRate());
            }
        });
        metrics.put(name(cacheName, "hitCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return Long.valueOf(cache.stats().hitCount());
            }
        });
        metrics.put(name(cacheName, "missCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return Long.valueOf(cache.stats().missCount());
            }
        });
        metrics.put(name(cacheName, "loadExceptionCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return Long.valueOf(cache.stats().loadExceptionCount());
            }
        });
        metrics.put(name(cacheName, "evictionCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return Long.valueOf(cache.stats().evictionCount());
            }
        });
        metrics.put(name(cacheName, "totalLoadTime"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return Long.valueOf(cache.stats().totalLoadTime());
            }
        });
    }

    public static MetricSet metricsFor(String cacheName, final Cache<?, ?> cache) {
        return new GuavaCacheMetrics(CACHE_PREFIX + cacheName, cache);
    }

    @Override
    public Map<String, Metric> getMetrics() {
        return metrics;
    }
}

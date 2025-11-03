package com.extole.common.metrics;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.Snapshot;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtoleMetricRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(ExtoleMetricRegistry.class);
    private static final Pattern NON_ALPHANUMERIC_OR_DOT = Pattern.compile("[^\\.a-zA-Z\\d]+");
    private static final String UNDERSCORE = "_";

    private final Map<String, ExtoleHistogram> histograms = Maps.newConcurrentMap();
    private final Map<String, ExtoleCounter> counters = Maps.newConcurrentMap();
    private final Map<String, ExtoleTimer> timers = Maps.newConcurrentMap();
    private final MetricRegistry metricRegistry;

    public ExtoleMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void registerAll(MetricSet metrics) {
        try {
            metricRegistry.registerAll(metrics);
        } catch (IllegalArgumentException e) {
            LOG.error("Trying to register a registered metric. Trying to register metrics: {}",
                metrics.getMetrics().keySet(), e);
        }
    }

    public <T extends Metric> T register(String name, T metric) {
        return metricRegistry.register(name, metric);
    }

    public ExtoleHistogram histogram(String name) {
        String metricName = getMetricName(name);
        ExtoleHistogram histogram = histograms.get(metricName);
        if (histogram == null) {
            histograms.putIfAbsent(metricName, new ExtoleHistogram(metricRegistry, metricName));
            histogram = histograms.get(metricName);
        }
        return histogram;
    }

    public ExtoleHistogram histogram(String name, Function<long[], Snapshot> snapshotProvider) {
        String metricName = getMetricName(name);
        ExtoleHistogram histogram = histograms.get(metricName);
        if (histogram == null) {
            histograms.putIfAbsent(metricName, new ExtoleHistogram(metricRegistry, metricName, snapshotProvider));
            histogram = histograms.get(metricName);
        }
        return histogram;
    }

    public ExtoleCounter counter(String name) {
        String metricName = getMetricName(name);
        ExtoleCounter counter = counters.get(metricName);
        if (counter == null) {
            counters.putIfAbsent(metricName, new ExtoleCounter(metricRegistry, metricName));
            counter = counters.get(metricName);
        }
        return counter;
    }

    public ExtoleTimer timer(String name) {
        String metricName = getMetricName(name);
        ExtoleTimer timer = timers.get(metricName);
        if (timer == null) {
            timers.putIfAbsent(metricName, new ExtoleTimer(metricRegistry, metricName));
            timer = timers.get(metricName);
        }
        return timer;
    }

    public Map<String, ExtoleCounter> getCounters() {
        return Collections.unmodifiableMap(counters);
    }

    public Map<String, ExtoleHistogram> getHistograms() {
        return Collections.unmodifiableMap(histograms);
    }

    private static String getMetricName(String name) {
        String cleanName = NON_ALPHANUMERIC_OR_DOT.matcher(name).replaceAll(UNDERSCORE);
        return MetricRegistry.name(cleanName);
    }
}

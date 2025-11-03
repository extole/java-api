package com.extole.common.metrics;

import java.time.Instant;

public interface HistogramMetricConsumer {
    void updateHistogram(String metricName, Instant start, Instant end);

    void updateHistogram(String metricName, long value);
}

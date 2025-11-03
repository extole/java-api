package com.extole.common.javascript;

import com.extole.common.metrics.ExtoleMetricRegistry;

class JavascriptMetrics {
    private final String metricName;

    JavascriptMetrics(String metricName) {
        this.metricName = metricName;
    }

    public void updateHistogram(ExtoleMetricRegistry metricRegistry, long histogramValue) {
        metricRegistry.histogram(metricName).update(histogramValue);
    }
}

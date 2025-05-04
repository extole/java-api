package com.extole.common.rest.util;

import java.io.IOException;

import org.apache.http.StatusLine;

import com.extole.common.metrics.ExtoleMetricRegistry;

public class MetricRetryNotificationListener implements RetryNotificationListener {
    private final ExtoleMetricRegistry metricRegistry;
    private final String metricPrefix;

    public MetricRetryNotificationListener(ExtoleMetricRegistry metricRegistry, String metricPrefix) {
        this.metricRegistry = metricRegistry;
        this.metricPrefix = metricPrefix;
    }

    @Override
    public void onRetry(StatusLine statusLine) {
        metricRegistry.counter(buildMetricName(String.valueOf(statusLine.getStatusCode()))).increment();
    }

    @Override
    public void onRetry(IOException exception) {
        metricRegistry.counter(buildMetricName(exception.getClass().getSimpleName())).increment();
    }

    private String buildMetricName(String retryReason) {
        return metricPrefix + "." + retryReason + ".count";
    }

}

package com.extole.client.rest.impl.events;

import com.extole.common.metrics.ExtoleMetricRegistry;

public enum EventDispatcherMetrics {
    @Deprecated // TODO to be removed in ENG-12938
    EVENT_CONVERSION_DURATION("event.conversion.duration.ms");

    private final String metric;

    EventDispatcherMetrics(String metric) {
        this.metric = metric;
    }

    public void updateDuration(ExtoleMetricRegistry metricRegistry, long durationMillis) {
        metricRegistry.histogram(metric).update(durationMillis);
    }
}

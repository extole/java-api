package com.extole.client.rest.impl.person.provider.legacy;

import com.extole.common.metrics.ExtoleMetricRegistry;

public enum ClientEventPersonLookupMetrics {

    PERSON_LOOKUP_DURATION("client.person.lookup.duration.ms"),
    PERSON_CREATE_DURATION("client.person.create.duration.ms");

    private final String metricName;

    ClientEventPersonLookupMetrics(String metricName) {
        this.metricName = metricName;
    }

    public void updateHistogram(ExtoleMetricRegistry metricRegistry, long histogramValue) {
        metricRegistry.histogram(metricName).update(histogramValue);
    }

}

package com.extole.consumer.rest.debug;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CreativeMetricRequest {
    private static final String KEY = "key";
    private static final String METRIC_TYPE = "metric_type";
    private static final String VALUE = "value";
    private final String key;
    private final CreativeMetricType metricType;
    private final Long value;

    @JsonCreator
    public CreativeMetricRequest(@JsonProperty(KEY) String key,
        @JsonProperty(METRIC_TYPE) CreativeMetricType metricType,
        @JsonProperty(VALUE) Long value) {
        this.key = key;
        this.metricType = metricType;
        this.value = value;
    }

    @JsonProperty(KEY)
    public String getKey() {
        return key;
    }

    @JsonProperty(METRIC_TYPE)
    public CreativeMetricType getMetricType() {
        return metricType;
    }

    @JsonProperty(VALUE)
    public Long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

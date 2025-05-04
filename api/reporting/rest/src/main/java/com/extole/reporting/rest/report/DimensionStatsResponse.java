package com.extole.reporting.rest.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class DimensionStatsResponse {
    private static final String JSON_VALUE = "value";
    private static final String JSON_COUNT = "count";

    private final String value;
    private final long count;

    @JsonCreator
    public DimensionStatsResponse(
        @JsonProperty(JSON_VALUE) String value,
        @JsonProperty(JSON_COUNT) long count) {
        this.value = value;
        this.count = count;
    }

    @JsonProperty(JSON_VALUE)
    public String getValue() {
        return value;
    }

    @JsonProperty(JSON_COUNT)
    public long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

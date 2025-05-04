package com.extole.reporting.rest.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ReportParameterResponse {
    private static final String JSON_VALUE = "value";
    private static final String JSON_TYPE = "details";

    private final String value;
    private final ReportParameterDetailsResponse details;

    @JsonCreator
    public ReportParameterResponse(
        @JsonProperty(JSON_VALUE) String value,
        @JsonProperty(JSON_TYPE) ReportParameterDetailsResponse details) {
        this.value = value;
        this.details = details;
    }

    @JsonProperty(JSON_VALUE)
    public String getValue() {
        return value;
    }

    @JsonProperty(JSON_TYPE)
    public ReportParameterDetailsResponse getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

package com.extole.reporting.rest.report.type;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.report.ParameterValueType;

public class StaticReportTypeParameterDetailsRequest extends ReportTypeParameterDetailsRequest {

    @JsonCreator
    public StaticReportTypeParameterDetailsRequest(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_ORDER) Optional<Integer> order,
        @JsonProperty(JSON_CATEGORY) Optional<String> category,
        @JsonProperty(JSON_IS_REQUIRED) Optional<Boolean> isRequired,
        @JsonProperty(JSON_DEFAULT_VALUE) Optional<String> defaultValue,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description) {
        super(ParameterValueType.STATIC, name, displayName, order, category, isRequired, defaultValue, description);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends
        ReportTypeParameterDetailsRequest.Builder<StaticReportTypeParameterDetailsRequest, Builder> {
        public StaticReportTypeParameterDetailsRequest build() {
            return new StaticReportTypeParameterDetailsRequest(name, displayName, order, category, isRequired,
                defaultValue, description);
        }
    }
}

package com.extole.reporting.rest.report.type;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.report.ParameterValueType;
import com.extole.reporting.rest.report.ReportParameterTypeName;

public class DynamicReportTypeParameterDetailsRequest extends ReportTypeParameterDetailsRequest {
    private static final String JSON_TYPE_NAME = "type_name";
    private static final String JSON_VALUES = "values";

    private final Optional<ReportParameterTypeName> typeName;
    private final Optional<Set<String>> values;

    @JsonCreator
    public DynamicReportTypeParameterDetailsRequest(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_ORDER) Optional<Integer> order,
        @JsonProperty(JSON_CATEGORY) Optional<String> category,
        @JsonProperty(JSON_IS_REQUIRED) Optional<Boolean> isRequired,
        @JsonProperty(JSON_DEFAULT_VALUE) Optional<String> defaultValue,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_TYPE_NAME) Optional<ReportParameterTypeName> typeName,
        @JsonProperty(JSON_VALUES) Optional<Set<String>> values) {
        super(ParameterValueType.DYNAMIC, name, displayName, order, category, isRequired, defaultValue, description);
        this.typeName = typeName;
        this.values = values;
    }

    @JsonProperty(JSON_TYPE_NAME)
    public Optional<ReportParameterTypeName> getTypeName() {
        return typeName;
    }

    @JsonProperty(JSON_VALUES)
    public Optional<Set<String>> getValues() {
        return values;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends ReportTypeParameterDetailsRequest.Builder<DynamicReportTypeParameterDetailsRequest, Builder> {
        private Optional<ReportParameterTypeName> typeName;
        private Optional<Set<String>> values = Optional.empty();

        private Builder() {
        }

        public Builder withTypeName(ReportParameterTypeName typeName) {
            this.typeName = Optional.ofNullable(typeName);
            return this;
        }

        public Builder withValues(Set<String> values) {
            this.values = Optional.ofNullable(values);
            return this;
        }

        public DynamicReportTypeParameterDetailsRequest build() {
            return new DynamicReportTypeParameterDetailsRequest(name, displayName, order, category, isRequired,
                defaultValue, description, typeName, values);
        }
    }
}

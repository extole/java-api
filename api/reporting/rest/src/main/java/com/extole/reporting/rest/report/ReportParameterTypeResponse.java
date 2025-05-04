package com.extole.reporting.rest.report;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ReportParameterTypeResponse {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE_TYPE = "value_type";
    private static final String JSON_VALUES = "values";

    private final ReportParameterTypeName name;
    private final ParameterValueType valueType;

    private final Set<String> values;

    public ReportParameterTypeResponse(
        @JsonProperty(JSON_NAME) ReportParameterTypeName name,
        @Nullable @JsonProperty(JSON_VALUE_TYPE) ParameterValueType valueType,
        @Nullable @JsonProperty(JSON_VALUES) Set<String> values) {
        this.name = name;
        this.valueType = valueType != null ? valueType : ParameterValueType.STATIC;
        this.values = values != null ? Collections.unmodifiableSet(values) : Collections.emptySet();
    }

    @JsonProperty(JSON_NAME)
    public ReportParameterTypeName getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE_TYPE)
    public ParameterValueType getValueType() {
        return valueType;
    }

    @JsonProperty(JSON_VALUES)
    public Set<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

package com.extole.reporting.rest.report;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportTypeParameterDetailsResponse {
    private static final String JSON_NAME = "name";
    private static final String JSON_DISPLAY_NAME = "display_name";
    private static final String JSON_CATEGORY = "category";
    private static final String JSON_TYPE = "type";
    private static final String JSON_IS_REQUIRED = "is_required";
    private static final String JSON_ORDER = "order";
    private static final String JSON_DEFAULT_VALUE = "default_value";
    private static final String JSON_DESCRIPTION = "description";

    private final String name;
    private final String displayName;
    private final String category;
    private final ReportParameterTypeResponse type;
    private final boolean isRequired;
    private final int order;
    private final String defaultValue;
    private final Optional<String> description;

    public ReportTypeParameterDetailsResponse(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) String displayName,
        @Nullable @JsonProperty(JSON_CATEGORY) String category,
        @JsonProperty(JSON_TYPE) ReportParameterTypeResponse type,
        @JsonProperty(JSON_IS_REQUIRED) boolean isRequired,
        @JsonProperty(JSON_ORDER) int order,
        @Nullable @JsonProperty(JSON_DEFAULT_VALUE) String defaultValue,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description) {
        this.name = name;
        this.displayName = displayName;
        this.category = category;
        this.type = type;
        this.isRequired = isRequired;
        this.order = order;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_DISPLAY_NAME)
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_CATEGORY)
    public String getCategory() {
        return category;
    }

    @JsonProperty(JSON_TYPE)
    public ReportParameterTypeResponse getType() {
        return type;
    }

    @JsonProperty(JSON_IS_REQUIRED)
    public boolean isRequired() {
        return isRequired;
    }

    @JsonProperty(JSON_ORDER)
    public int getOrder() {
        return order;
    }

    @JsonProperty(JSON_DEFAULT_VALUE)
    public String getDefaultValue() {
        return defaultValue;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }
}

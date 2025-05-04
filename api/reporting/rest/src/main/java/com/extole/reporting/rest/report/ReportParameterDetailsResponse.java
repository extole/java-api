package com.extole.reporting.rest.report;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ReportParameterDetailsResponse {
    private static final String JSON_NAME = "name";
    private static final String JSON_DISPLAY_NAME = "display_name";
    private static final String JSON_CATEGORY = "category";
    private static final String JSON_TYPE = "type";
    private static final String JSON_IS_REQUIRED = "is_required";
    private static final String JSON_ORDER = "order";

    private final String name;
    private final String displayName;
    private final String category;
    private final ReportParameterTypeResponse type;
    private final boolean isRequired;
    private final int order;

    @JsonCreator
    public ReportParameterDetailsResponse(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) String displayName,
        @Nullable @JsonProperty(JSON_CATEGORY) String category,
        @JsonProperty(JSON_TYPE) ReportParameterTypeResponse type,
        @JsonProperty(JSON_IS_REQUIRED) boolean isRequired,
        @JsonProperty(JSON_ORDER) int order) {
        this.name = name;
        this.displayName = displayName;
        this.category = category;
        this.type = type;
        this.isRequired = isRequired;
        this.order = order;
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

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

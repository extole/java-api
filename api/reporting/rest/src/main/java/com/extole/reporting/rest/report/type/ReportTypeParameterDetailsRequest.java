package com.extole.reporting.rest.report.type;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.reporting.rest.report.ParameterValueType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ReportTypeParameterDetailsRequest.JSON_TYPE, defaultImpl = StaticReportTypeParameterDetailsRequest.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StaticReportTypeParameterDetailsRequest.class, name = "STATIC"),
    @JsonSubTypes.Type(value = DynamicReportTypeParameterDetailsRequest.class, name = "DYNAMIC")
})
public class ReportTypeParameterDetailsRequest {

    protected static final String JSON_TYPE = "type";
    protected static final String JSON_NAME = "name";
    protected static final String JSON_DISPLAY_NAME = "display_name";
    protected static final String JSON_ORDER = "order";
    protected static final String JSON_CATEGORY = "category";
    protected static final String JSON_IS_REQUIRED = "is_required";
    protected static final String JSON_DEFAULT_VALUE = "default_value";
    protected static final String JSON_DESCRIPTION = "description";

    private final ParameterValueType type;
    private final String name;
    private final Optional<String> displayName;
    private final Optional<Integer> order;
    private final Optional<String> category;
    private final Optional<Boolean> isRequired;
    private final Optional<String> defaultValue;
    private final Optional<String> description;

    @JsonCreator
    public ReportTypeParameterDetailsRequest(
        @JsonProperty(JSON_TYPE) ParameterValueType type,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_ORDER) Optional<Integer> order,
        @JsonProperty(JSON_CATEGORY) Optional<String> category,
        @JsonProperty(JSON_IS_REQUIRED) Optional<Boolean> isRequired,
        @JsonProperty(JSON_DEFAULT_VALUE) Optional<String> defaultValue,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.order = order;
        this.category = category;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    @JsonProperty(JSON_TYPE)
    public ParameterValueType getType() {
        return type;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_ORDER)
    public Optional<Integer> getOrder() {
        return order;
    }

    @JsonProperty(JSON_CATEGORY)
    public Optional<String> getCategory() {
        return category;
    }

    @JsonProperty(JSON_IS_REQUIRED)
    public Optional<Boolean> getIsRequired() {
        return isRequired;
    }

    @JsonProperty(JSON_DEFAULT_VALUE)
    public Optional<String> getDefaultValue() {
        return defaultValue;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    public abstract static class Builder<R extends ReportTypeParameterDetailsRequest, T extends Builder<R, T>> {
        protected String name;
        protected Optional<String> displayName = Optional.empty();
        protected Optional<Integer> order = Optional.empty();
        protected Optional<String> category = Optional.empty();
        protected Optional<Boolean> isRequired = Optional.empty();
        protected Optional<String> defaultValue = Optional.empty();
        protected Optional<String> description = Optional.empty();

        protected Builder() {
        }

        public T withName(String name) {
            this.name = name;
            return (T) this;
        }

        public T withDisplayName(String displayName) {
            this.displayName = Optional.ofNullable(displayName);
            return (T) this;
        }

        public T withOrder(Integer order) {
            this.order = Optional.ofNullable(order);
            return (T) this;
        }

        public T withCategory(String category) {
            this.category = Optional.ofNullable(category);
            return (T) this;
        }

        public T withIsRequired(Boolean isRequired) {
            this.isRequired = Optional.ofNullable(isRequired);
            return (T) this;
        }

        public T withDefaultValue(String defaultValue) {
            this.defaultValue = Optional.ofNullable(defaultValue);
            return (T) this;
        }

        public T withDescription(String description) {
            this.description = Optional.ofNullable(description);
            return (T) this;
        }

        public abstract R build();
    }
}

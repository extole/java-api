package com.extole.reporting.rest.impl.report.type.uploaders;

import java.util.Objects;
import java.util.Optional;

import com.extole.model.entity.report.type.ReportParameterType;
import com.extole.model.entity.report.type.ReportTypeParameterDetails;

public final class ReportTypeParameterDetailsImpl implements ReportTypeParameterDetails {
    private final String name;
    private final String displayName;
    private final int order;
    private final Optional<String> category;
    private final ReportParameterType type;
    private final boolean isRequired;
    private final Optional<String> defaultValue;
    private final Optional<String> description;

    private ReportTypeParameterDetailsImpl(String name, String displayName, int order, Optional<String> category,
        ReportParameterType type, boolean isRequired, Optional<String> defaultValue,
        Optional<String> description) {
        this.name = name;
        this.displayName = displayName;
        this.order = order;
        this.category = category;
        this.type = type;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Optional<String> getCategory() {
        return category;
    }

    @Override
    public ReportParameterType getType() {
        return type;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public Optional<String> getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Optional<String> getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ReportTypeParameterDetailsImpl that = (ReportTypeParameterDetailsImpl) other;
        return order == that.order &&
            isRequired == that.isRequired &&
            name.equals(that.name) &&
            displayName.equals(that.displayName) &&
            category.equals(that.category) &&
            type.equals(that.type) &&
            defaultValue.equals(that.defaultValue) &&
            description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, displayName, Integer.valueOf(order), category, type, Boolean.valueOf(isRequired),
            defaultValue, description);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private static final int DEFAULT_ORDER = 100;
        private String name;
        private String displayName;
        private int order = DEFAULT_ORDER;
        private Optional<String> category = Optional.empty();
        private ReportParameterType type = ReportParameterType.STRING;
        private Optional<Boolean> isRequired = Optional.empty();
        private Optional<String> defaultValue = Optional.empty();
        private Optional<String> description = Optional.empty();

        private Builder() {

        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withOrder(int order) {
            this.order = order;
            return this;
        }

        public Builder withCategory(String category) {
            this.category = Optional.ofNullable(category);
            return this;
        }

        public Builder withType(ReportParameterType type) {
            this.type = type;
            return this;
        }

        public Builder withIsRequired(Boolean isRequired) {
            this.isRequired = Optional.ofNullable(isRequired);
            return this;
        }

        public Builder withDefaultValue(String defaultValue) {
            this.defaultValue = Optional.ofNullable(defaultValue);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Optional.ofNullable(description);
            return this;
        }

        public ReportTypeParameterDetailsImpl build() {
            return new ReportTypeParameterDetailsImpl(name, displayName, order, category, type,
                isRequired.orElse(Boolean.FALSE).booleanValue(),
                defaultValue, description);
        }

    }
}

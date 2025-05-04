package com.extole.client.rest.prehandler.action;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.prehandler.PrehandlerContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.Evaluatable;

public class DataAttributeMapping {

    private static final String JSON_ATTRIBUTE = "attribute";
    private static final String JSON_SOURCE_ATTRIBUTE = "source_attribute";
    private static final String JSON_DEFAULT_VALUE = "default_value";

    private final String attribute;
    private final String sourceAttribute;
    private final Evaluatable<PrehandlerContext, String> defaultValue;

    @JsonCreator
    public DataAttributeMapping(@JsonProperty(JSON_ATTRIBUTE) String attribute,
        @JsonProperty(JSON_SOURCE_ATTRIBUTE) String sourceAttribute,
        @Nullable @JsonProperty(JSON_DEFAULT_VALUE) Evaluatable<PrehandlerContext, String> defaultValue) {
        this.attribute = attribute;
        this.sourceAttribute = sourceAttribute;
        this.defaultValue = defaultValue;
    }

    @JsonProperty(JSON_ATTRIBUTE)
    public String getAttribute() {
        return attribute;
    }

    @JsonProperty(JSON_SOURCE_ATTRIBUTE)
    public String getSourceAttribute() {
        return sourceAttribute;
    }

    @JsonProperty(JSON_DEFAULT_VALUE)
    public Optional<Evaluatable<PrehandlerContext, String>> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String attribute;
        private String sourceAttribute;
        private Evaluatable<PrehandlerContext, String> defaultValue;

        public Builder withAttribute(String attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder withSourceAttribute(String sourceAttribute) {
            this.sourceAttribute = sourceAttribute;
            return this;
        }

        public Builder withDefaultValue(Evaluatable<PrehandlerContext, String> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public DataAttributeMapping build() {
            return new DataAttributeMapping(attribute, sourceAttribute, defaultValue);
        }
    }
}

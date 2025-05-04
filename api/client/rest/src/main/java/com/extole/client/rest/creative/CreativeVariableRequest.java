package com.extole.client.rest.creative;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public final class CreativeVariableRequest {
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_VALUES = "values";
    private static final String JSON_VISIBLE = "visible";

    private final Omissible<CreativeVariableScope> scope;
    private final Omissible<Map<String, String>> values;
    private final Omissible<Boolean> visible;

    @JsonCreator
    private CreativeVariableRequest(
        @JsonProperty(JSON_SCOPE) Omissible<CreativeVariableScope> scope,
        @JsonProperty(JSON_VALUES) Omissible<Map<String, String>> values,
        @JsonProperty(JSON_VISIBLE) Omissible<Boolean> visible) {
        this.scope = scope;
        this.values = values;
        this.visible = visible;
    }

    @JsonProperty(JSON_SCOPE)
    public Omissible<CreativeVariableScope> getScope() {
        return scope;
    }

    @JsonProperty(JSON_VALUES)
    public Omissible<Map<String, String>> getValues() {
        return values;
    }

    @JsonProperty(JSON_VISIBLE)
    public Omissible<Boolean> getVisible() {
        return visible;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CreativeVariableScope scope;
        private Map<String, String> values;
        private Boolean visible;

        private Builder() {
        }

        public Builder withScope(CreativeVariableScope scope) {
            this.scope = scope;
            return this;
        }

        public Builder withValues(Map<String, String> values) {
            this.values = values;
            return this;
        }

        public Builder withVisible(boolean visible) {
            this.visible = Boolean.valueOf(visible);
            return this;
        }

        public CreativeVariableRequest build() {
            return new CreativeVariableRequest(
                scope == null ? Omissible.omitted() : Omissible.of(scope),
                values == null ? Omissible.omitted() : Omissible.of(values),
                visible == null ? Omissible.omitted() : Omissible.of(visible));
        }
    }

}

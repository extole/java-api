package com.extole.client.rest.impl.creative.batch;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import com.extole.client.rest.creative.CreativeVariableScope;

public final class VariableUpdateRequest {

    private final String name;
    private final Optional<Map<String, String>> values;
    private final Optional<CreativeVariableScope> scope;
    private final Optional<Boolean> visible;

    public VariableUpdateRequest(
        String name,
        Optional<CreativeVariableScope> scope,
        Optional<Boolean> visible,
        Optional<Map<String, String>> values) {
        this.name = name;
        this.scope = scope;
        this.visible = visible;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public Optional<CreativeVariableScope> getScope() {
        return scope;
    }

    public Optional<Map<String, String>> getValues() {
        return values;
    }

    public Optional<Boolean> getVisible() {
        return visible;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        protected String name;
        protected Optional<CreativeVariableScope> scope = Optional.empty();
        protected Optional<Map<String, String>> values = Optional.empty();
        protected Optional<Boolean> visible = Optional.empty();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withValues(Map<String, String> values) {
            this.values = Optional.of(ImmutableMap.copyOf(values));
            return this;
        }

        public Builder withScope(CreativeVariableScope scope) {
            this.scope = Optional.of(scope);
            return this;
        }

        public Builder withVisible(boolean visible) {
            this.visible = Optional.of(Boolean.valueOf(visible));
            return this;
        }

        public VariableUpdateRequest build() {
            return new VariableUpdateRequest(name, scope, visible, values);
        }

    }

}

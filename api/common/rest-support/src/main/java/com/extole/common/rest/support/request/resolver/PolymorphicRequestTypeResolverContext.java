package com.extole.common.rest.support.request.resolver;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import com.extole.authorization.service.Authorization;

public final class PolymorphicRequestTypeResolverContext {

    private final Map<String, String> parameters;
    private final Authorization authorization;

    private PolymorphicRequestTypeResolverContext(Map<String, String> parameters, Authorization authorization) {
        this.parameters = ImmutableMap.copyOf(parameters);
        this.authorization = authorization;
    }

    public Optional<String> getValue(String key) {
        return Optional.ofNullable(parameters.get(key));
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Authorization authorization;
        private Map<String, String> parameters;

        public Builder withAuthorization(Authorization authorization) {
            this.authorization = authorization;
            return this;
        }

        public Builder withParameters(Map<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public PolymorphicRequestTypeResolverContext build() {
            return new PolymorphicRequestTypeResolverContext(parameters, authorization);
        }
    }
}

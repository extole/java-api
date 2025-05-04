package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

public class MemberRequest {

    private static final String EMAIL = "email";
    private static final String IDENTITY_KEY_VALUE = "identity_key_value";
    private static final String DATA = "data";

    private final String identityKeyValue;
    private final Map<String, String> data;

    public MemberRequest(
        @JsonProperty(EMAIL) Optional<String> email,
        @JsonProperty(IDENTITY_KEY_VALUE) Optional<String> identityKeyValue,
        @JsonProperty(DATA) Map<String, String> data) {
        this.identityKeyValue = identityKeyValue.orElse(email.orElse(null));
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(IDENTITY_KEY_VALUE)
    public String getIdentityKeyValue() {
        return identityKeyValue;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String identityKeyValue;
        private Map<String, String> data = new HashMap<>();

        private Builder() {
        }

        public Builder withIdentityKeyValue(String identityKeyValue) {
            this.identityKeyValue = identityKeyValue;
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public MemberRequest build() {
            return new MemberRequest(Optional.empty(), Optional.ofNullable(identityKeyValue), data);
        }

    }

}

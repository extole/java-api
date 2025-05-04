package com.extole.client.rest.auth.method;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class AuthMethodDiscoverRequest {

    private static final String EMAIL = "email";

    private final String email;

    public AuthMethodDiscoverRequest(@Nullable @JsonProperty(EMAIL) String email) {
        this.email = email;
    }

    @Nullable
    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String email;

        private Builder() {

        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public AuthMethodDiscoverRequest build() {
            return new AuthMethodDiscoverRequest(email);
        }
    }

}

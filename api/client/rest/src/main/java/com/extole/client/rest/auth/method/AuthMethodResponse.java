package com.extole.client.rest.auth.method;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.auth.provider.type.AuthProviderTypeProtocol;
import com.extole.common.lang.ToString;

public final class AuthMethodResponse {

    private static final String NAME = "name";
    private static final String PROTOCOL = "protocol";
    private static final String AUTH_URL = "auth_url";
    private static final String DATA = "data";
    private static final String DESCRIPTION = "description";

    private final String name;
    private final AuthProviderTypeProtocol authProviderTypeProtocol;
    private final String authUrl;
    private final Map<String, String> data;
    private final String description;

    public static Builder builder() {
        return new Builder();
    }

    public AuthMethodResponse(
        @JsonProperty(NAME) String name,
        @JsonProperty(PROTOCOL) AuthProviderTypeProtocol authProviderTypeProtocol,
        @JsonProperty(AUTH_URL) String authUrl,
        @JsonProperty(DATA) Map<String, String> data,
        @Nullable @JsonProperty(DESCRIPTION) String description) {
        this.name = name;
        this.authProviderTypeProtocol = authProviderTypeProtocol;
        this.authUrl = authUrl;
        this.data = data;
        this.description = description;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(PROTOCOL)
    public AuthProviderTypeProtocol getAuthProviderTypeProtocol() {
        return authProviderTypeProtocol;
    }

    @JsonProperty(AUTH_URL)
    public String getAuthUrl() {
        return authUrl;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Nullable
    @JsonProperty(DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private String name;
        private AuthProviderTypeProtocol authProviderTypeProtocol;
        private String authUrl;
        private final Map<String, String> data = new HashMap<>();
        private String description;

        private Builder() {

        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAuthProviderTypeProtocol(AuthProviderTypeProtocol authProviderTypeProtocol) {
            this.authProviderTypeProtocol = authProviderTypeProtocol;
            return this;
        }

        public Builder withAuthUrl(String authUrl) {
            this.authUrl = authUrl;
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data.clear();
            this.data.putAll(data == null ? Collections.emptyMap() : data);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public AuthMethodResponse build() {
            return new AuthMethodResponse(name,
                authProviderTypeProtocol,
                authUrl,
                data,
                description);
        }

    }
}

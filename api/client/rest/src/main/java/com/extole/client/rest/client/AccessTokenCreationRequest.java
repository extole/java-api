package com.extole.client.rest.client;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

public class AccessTokenCreationRequest {

    private static final String JSON_EMAIL = "email";
    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_PASSWORD = "password";
    private static final String JSON_CLIENT_ID = "client_id";
    private static final String JSON_DURATION_SECONDS = "duration_seconds";

    private final String email;
    private final String clientId;
    private final String password;
    private final Set<Scope> scopes;
    private final Long durationSeconds;

    @JsonCreator
    AccessTokenCreationRequest(@JsonProperty(JSON_CLIENT_ID) String clientId,
        @Nullable @JsonProperty(JSON_EMAIL) String email,
        @Nullable @JsonProperty(JSON_PASSWORD) String password,
        @Nullable @JsonProperty(JSON_SCOPES) Set<Scope> scopes,
        @Nullable @JsonProperty(JSON_DURATION_SECONDS) Long durationSeconds) {
        this.email = email;
        this.password = password;
        this.clientId = clientId;
        this.scopes = scopes == null ? Collections.emptySet() : ImmutableSet.copyOf(scopes);
        this.durationSeconds = durationSeconds;
    }

    public AccessTokenCreationRequest(String clientId, String email, String password, Set<Scope> scopes) {
        this(clientId, email, password, scopes, null);
    }

    @JsonProperty(JSON_CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(JSON_EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(JSON_PASSWORD)
    public String getPassword() {
        return password;
    }

    @JsonProperty(JSON_SCOPES)
    public Set<Scope> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_DURATION_SECONDS)
    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String email;
        private String clientId;
        private String password;
        private Set<Scope> scopes;
        private Long durationSeconds;

        private Builder() {
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withScopes(Set<Scope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder withDurationSeconds(Long durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public AccessTokenCreationRequest build() {
            return new AccessTokenCreationRequest(clientId, email, password, scopes, durationSeconds);
        }
    }
}

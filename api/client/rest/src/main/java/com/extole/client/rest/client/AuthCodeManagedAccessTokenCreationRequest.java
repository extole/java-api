package com.extole.client.rest.client;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class AuthCodeManagedAccessTokenCreationRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_DURATION_SECONDS = "duration_seconds";
    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_CODE = "code";
    private static final String JSON_STATE = "state";

    private final String name;
    private final Long durationSeconds;
    private final Set<Scope> scopes;
    private final String code;
    private final String state;

    @JsonCreator
    public AuthCodeManagedAccessTokenCreationRequest(@JsonProperty(JSON_NAME) String name,
        @Nullable @JsonProperty(JSON_DURATION_SECONDS) Long durationSeconds,
        @Nullable @JsonProperty(JSON_SCOPES) Set<Scope> scopes,
        @JsonProperty(JSON_CODE) String code,
        @JsonProperty(JSON_STATE) String state) {
        this.name = name;
        this.durationSeconds = durationSeconds;
        this.scopes = Collections.unmodifiableSet(scopes);
        this.code = code;
        this.state = state;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @JsonProperty(JSON_DURATION_SECONDS)
    public Long getDurationSeconds() {
        return durationSeconds;
    }

    @Nullable
    @JsonProperty(JSON_SCOPES)
    public Set<Scope> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_STATE)
    public String getState() {
        return state;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private Long durationSeconds;
        private Set<Scope> scopes;
        private String code;
        private String state;

        private Builder() {

        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDurationSeconds(Long durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withScopes(Set<Scope> scopes) {
            this.scopes = Collections.unmodifiableSet(scopes);
            return this;
        }

        public Builder withState(String state) {
            this.state = state;
            return this;
        }

        public AuthCodeManagedAccessTokenCreationRequest build() {
            return new AuthCodeManagedAccessTokenCreationRequest(name, durationSeconds, scopes, code, state);
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}

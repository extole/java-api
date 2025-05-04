package com.extole.client.rest.client;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class ManagedAccessTokenCreationRequest {

    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_DURATION_SECONDS = "duration_seconds";
    private static final String JSON_NAME = "name";
    private static final String JSON_PASSWORD = "password";

    private final String password;
    private final Omissible<String> name;
    private final Omissible<Long> durationSeconds;
    private final Omissible<Set<Scope>> scopes;

    @JsonCreator
    ManagedAccessTokenCreationRequest(@JsonProperty(JSON_PASSWORD) String password,
        @JsonProperty(JSON_NAME) Omissible<String> name,
        @JsonProperty(JSON_DURATION_SECONDS) Omissible<Long> durationSeconds,
        @JsonProperty(JSON_SCOPES) Omissible<Set<Scope>> scopes) {
        this.password = password;
        this.name = name;
        this.durationSeconds = durationSeconds;
        this.scopes = scopes;
    }

    @JsonProperty(JSON_PASSWORD)
    public String getPassword() {
        return password;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_DURATION_SECONDS)
    public Omissible<Long> getDurationSeconds() {
        return durationSeconds;
    }

    @JsonProperty(JSON_SCOPES)
    public Omissible<Set<Scope>> getScopes() {
        return scopes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String password;
        private Omissible<Set<Scope>> scopes = Omissible.omitted();
        private Omissible<Long> durationSeconds = Omissible.omitted();
        private Omissible<String> name = Omissible.omitted();

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withScopes(Set<Scope> scopes) {
            this.scopes = Omissible.of(Set.copyOf(scopes));
            return this;
        }

        public Builder withDurationSeconds(Long durationSeconds) {
            this.durationSeconds = Omissible.of(durationSeconds);
            return this;
        }

        public Builder withName(String name) {
            this.name = Omissible.of(name);
            return this;
        }

        public ManagedAccessTokenCreationRequest build() {
            return new ManagedAccessTokenCreationRequest(password, name, durationSeconds, scopes);
        }
    }
}

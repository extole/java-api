package com.extole.client.rest.user;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse {
    private static final String JSON_USER_ID = "user_id";
    private static final String JSON_EMAIL = "email";
    private static final String JSON_FIRST_NAME = "first_name";
    private static final String JSON_LAST_NAME = "last_name";
    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_LAST_LOGGED_IN = "last_logged_in";
    private static final String JSON_LOCKED = "locked";

    private final String userId;
    private final String email;
    private final Optional<String> firstName;
    private final Optional<String> lastName;
    private final Set<UserScope> scopes;
    private final Optional<String> lastLoggedIn;
    private final boolean locked;

    @JsonCreator
    UserResponse(@JsonProperty(JSON_USER_ID) String userId,
        @JsonProperty(JSON_EMAIL) String email,
        @JsonProperty(JSON_FIRST_NAME) Optional<String> firstName,
        @JsonProperty(JSON_LAST_NAME) Optional<String> lastName,
        @JsonProperty(JSON_SCOPES) Set<UserScope> scopes,
        @JsonProperty(JSON_LAST_LOGGED_IN) Optional<String> lastLoggedIn,
        @JsonProperty(JSON_LOCKED) boolean locked) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.scopes = scopes;
        this.lastLoggedIn = lastLoggedIn;
        this.locked = locked;
    }

    @JsonProperty(JSON_USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(JSON_EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(JSON_FIRST_NAME)
    public Optional<String> getFirstName() {
        return firstName;
    }

    @JsonProperty(JSON_LAST_NAME)
    public Optional<String> getLastName() {
        return lastName;
    }

    @JsonProperty(JSON_SCOPES)
    public Set<UserScope> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_LAST_LOGGED_IN)
    public Optional<String> getLastLoggedIn() {
        return lastLoggedIn;
    }

    @JsonProperty(JSON_LOCKED)
    public boolean isLocked() {
        return locked;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String email;
        private Optional<String> firstName = Optional.empty();
        private Optional<String> lastName = Optional.empty();
        private Set<UserScope> scopes;
        private Optional<String> lastLoggedIn = Optional.empty();
        private boolean locked;

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = Optional.ofNullable(firstName);
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = Optional.ofNullable(lastName);
            return this;
        }

        public Builder withScopes(Set<UserScope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder withLastLoggedIn(String lastLoggedIn) {
            this.lastLoggedIn = Optional.ofNullable(lastLoggedIn);
            return this;
        }

        public Builder withLocked(boolean locked) {
            this.locked = locked;
            return this;
        }

        public UserResponse build() {
            return new UserResponse(userId, email, firstName, lastName, scopes, lastLoggedIn, locked);
        }
    }
}

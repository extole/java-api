package com.extole.client.rest.user;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class UserCreationRequest {
    private static final String JSON_EMAIL = "email";
    private static final String JSON_PASSWORD = "password";
    private static final String JSON_FIRST_NAME = "first_name";
    private static final String JSON_LAST_NAME = "last_name";
    private static final String JSON_SCOPES = "scopes";

    private final String email;
    private final Omissible<String> password;
    private final Omissible<String> firstName;
    private final Omissible<String> lastName;
    private final Omissible<Set<UserScope>> scopes;

    UserCreationRequest(@JsonProperty(JSON_EMAIL) String email,
        @JsonProperty(JSON_PASSWORD) Omissible<String> password,
        @JsonProperty(JSON_FIRST_NAME) Omissible<String> firstName,
        @JsonProperty(JSON_LAST_NAME) Omissible<String> lastName,
        @JsonProperty(JSON_SCOPES) Omissible<Set<UserScope>> scopes) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.scopes = scopes;
    }

    @JsonProperty(JSON_EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(JSON_PASSWORD)
    public Omissible<String> getPassword() {
        return password;
    }

    @JsonProperty(JSON_FIRST_NAME)
    public Omissible<String> getFirstName() {
        return firstName;
    }

    @JsonProperty(JSON_LAST_NAME)
    public Omissible<String> getLastName() {
        return lastName;
    }

    @JsonProperty(JSON_SCOPES)
    public Omissible<Set<UserScope>> getScopes() {
        return scopes;
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
        private Omissible<String> password = Omissible.omitted();
        private Omissible<String> firstName = Omissible.omitted();
        private Omissible<String> lastName = Omissible.omitted();
        private Omissible<Set<UserScope>> scopes = Omissible.omitted();

        private Builder() {
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = Omissible.of(password);
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = Omissible.of(firstName);
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = Omissible.of(lastName);
            return this;
        }

        public Builder withScopes(Set<UserScope> scopes) {
            this.scopes = Omissible.of(scopes);
            return this;
        }

        public UserCreationRequest build() {
            return new UserCreationRequest(email, password, firstName, lastName, scopes);
        }
    }
}

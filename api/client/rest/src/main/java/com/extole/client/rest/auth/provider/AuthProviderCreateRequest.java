package com.extole.client.rest.auth.provider;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class AuthProviderCreateRequest {

    private static final String NAME = "name";
    private static final String AUTH_PROVIDER_TYPE_ID = "auth_provider_type_id";
    private static final String DEFAULT_ENABLED_FOR_ALL_USERS = "default_enabled_for_all_users";
    private static final String DESCRIPTION = "description";

    private final String name;
    private final String authProviderTypeId;
    private final Boolean defaultEnabledForAllUsers;
    private final String description;

    public AuthProviderCreateRequest(@JsonProperty(NAME) String name,
        @JsonProperty(AUTH_PROVIDER_TYPE_ID) String authProviderTypeId,
        @Nullable @JsonProperty(DEFAULT_ENABLED_FOR_ALL_USERS) Boolean defaultEnabledForAllUsers,
        @Nullable @JsonProperty(DESCRIPTION) String description) {
        this.name = name;
        this.authProviderTypeId = authProviderTypeId;
        this.defaultEnabledForAllUsers = defaultEnabledForAllUsers;
        this.description = description;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(AUTH_PROVIDER_TYPE_ID)
    public String getAuthProviderTypeId() {
        return authProviderTypeId;
    }

    @Nullable
    @JsonProperty(DEFAULT_ENABLED_FOR_ALL_USERS)
    public Boolean isDefaultEnabledForAllUsers() {
        return defaultEnabledForAllUsers;
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String authProviderTypeId;
        private Boolean defaultEnabledForAllUsers;
        private String description;

        private Builder() {

        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAuthProviderTypeId(String authProviderTypeId) {
            this.authProviderTypeId = authProviderTypeId;
            return this;
        }

        public Builder withDefaultEnabledForAllUsers(Boolean defaultEnabledForAllUsers) {
            this.defaultEnabledForAllUsers = defaultEnabledForAllUsers;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public AuthProviderCreateRequest build() {
            return new AuthProviderCreateRequest(name,
                authProviderTypeId,
                defaultEnabledForAllUsers,
                description);
        }
    }

}

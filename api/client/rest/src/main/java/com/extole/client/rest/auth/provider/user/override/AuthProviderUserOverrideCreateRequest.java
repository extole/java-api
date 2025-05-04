package com.extole.client.rest.auth.provider.user.override;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class AuthProviderUserOverrideCreateRequest {

    private static final String NAME = "name";
    private static final String USER_ID = "user_id";
    private static final String AUTH_PROVIDER_ENABLED_FOR_USER = "auth_provider_enabled_for_user";
    private static final String DESCRIPTION = "description";

    private final String name;
    private final String userId;
    private final Boolean authProviderEnabledForUser;
    private final String description;

    public AuthProviderUserOverrideCreateRequest(@Nullable @JsonProperty(NAME) String name,
        @JsonProperty(USER_ID) String userId,
        @Nullable @JsonProperty(AUTH_PROVIDER_ENABLED_FOR_USER) Boolean authProviderEnabledForUser,
        @Nullable @JsonProperty(DESCRIPTION) String description) {
        this.name = name;
        this.userId = userId;
        this.authProviderEnabledForUser = authProviderEnabledForUser;
        this.description = description;
    }

    @Nullable
    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
    }

    @Nullable
    @JsonProperty(AUTH_PROVIDER_ENABLED_FOR_USER)
    public Boolean isAuthProviderEnabledForUser() {
        return authProviderEnabledForUser;
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
        private String userId;
        private Boolean authProviderEnabledForUser;
        private String description;

        private Builder() {

        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withAuthProviderEnabledForUser(Boolean authProviderEnabledForUser) {
            this.authProviderEnabledForUser = authProviderEnabledForUser;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public AuthProviderUserOverrideCreateRequest build() {
            return new AuthProviderUserOverrideCreateRequest(name,
                userId,
                authProviderEnabledForUser,
                description);
        }
    }

}

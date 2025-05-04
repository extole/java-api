package com.extole.client.rest.auth.provider.user.override;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class AuthProviderUserOverrideResponse {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String USER_ID = "user_id";
    private static final String AUTH_PROVIDER_ENABLED_FOR_USER = "auth_provider_enabled_for_user";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private final String id;
    private final String name;
    private final String userId;
    private final boolean authProviderEnabledForUser;
    private final String description;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public AuthProviderUserOverrideResponse(@JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(USER_ID) String userId,
        @JsonProperty(AUTH_PROVIDER_ENABLED_FOR_USER) boolean authProviderEnabledForUser,
        @Nullable @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.authProviderEnabledForUser = authProviderEnabledForUser;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(AUTH_PROVIDER_ENABLED_FOR_USER)
    public boolean isAuthProviderEnabledForUser() {
        return authProviderEnabledForUser;
    }

    @Nullable
    @JsonProperty(DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(UPDATED_AT)
    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}

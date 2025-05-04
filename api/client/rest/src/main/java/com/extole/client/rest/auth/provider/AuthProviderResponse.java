package com.extole.client.rest.auth.provider;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class AuthProviderResponse {

    private static final String AUTH_PROVIDER_ID = "id";
    private static final String NAME = "name";
    private static final String AUTH_PROVIDER_TYPE_ID = "auth_provider_type_id";
    private static final String DEFAULT_ENABLED_FOR_ALL_USERS = "default_enabled_for_all_users";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private final String id;
    private final String name;
    private final String authProviderTypeId;
    private final boolean defaultEnabledForAllUsers;
    private final String description;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public AuthProviderResponse(@JsonProperty(AUTH_PROVIDER_ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(AUTH_PROVIDER_TYPE_ID) String authProviderTypeId,
        @JsonProperty(DEFAULT_ENABLED_FOR_ALL_USERS) boolean defaultEnabledForAllUsers,
        @Nullable @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.authProviderTypeId = authProviderTypeId;
        this.defaultEnabledForAllUsers = defaultEnabledForAllUsers;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @JsonProperty(AUTH_PROVIDER_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(AUTH_PROVIDER_TYPE_ID)
    public String getAuthProviderTypeId() {
        return authProviderTypeId;
    }

    @JsonProperty(DEFAULT_ENABLED_FOR_ALL_USERS)
    public boolean isDefaultEnabledForAllUsers() {
        return defaultEnabledForAllUsers;
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

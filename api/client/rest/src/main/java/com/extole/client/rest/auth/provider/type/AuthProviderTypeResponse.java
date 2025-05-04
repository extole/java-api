package com.extole.client.rest.auth.provider.type;

import java.time.ZonedDateTime;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.client.Scope;
import com.extole.common.lang.ToString;

public class AuthProviderTypeResponse {

    private static final String AUTH_PROVIDER_TYPE_ID = "id";
    private static final String NAME = "name";
    private static final String PROTOCOL = "protocol";
    private static final String SCOPES = "scopes";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private final String id;
    private final String name;
    private final AuthProviderTypeProtocol authProviderTypeProtocol;
    private final Set<Scope> scopes;
    private final String description;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public AuthProviderTypeResponse(@JsonProperty(AUTH_PROVIDER_TYPE_ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(PROTOCOL) AuthProviderTypeProtocol authProviderTypeProtocol,
        @JsonProperty(SCOPES) Set<Scope> scopes,
        @Nullable @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.authProviderTypeProtocol = authProviderTypeProtocol;
        this.scopes = scopes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
    }

    @JsonProperty(AUTH_PROVIDER_TYPE_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(PROTOCOL)
    public AuthProviderTypeProtocol getAuthProviderTypeProtocol() {
        return authProviderTypeProtocol;
    }

    @JsonProperty(SCOPES)
    public Set<Scope> getScopes() {
        return scopes;
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

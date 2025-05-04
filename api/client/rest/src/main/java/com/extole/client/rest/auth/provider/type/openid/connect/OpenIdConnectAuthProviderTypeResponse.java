package com.extole.client.rest.auth.provider.type.openid.connect;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.client.Scope;
import com.extole.common.lang.ToString;

public class OpenIdConnectAuthProviderTypeResponse {

    private static final String AUTH_PROVIDER_TYPE_ID = "id";
    private static final String NAME = "name";
    private static final String DOMAIN = "domain";
    private static final String APPLICATION_ID = "application_id";
    private static final String APPLICATION_SECRET = "application_secret";
    private static final String CUSTOM_PARAMS = "custom_params";
    private static final String SCOPES = "scopes";
    private static final String CATEGORY = "category";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private final String id;
    private final String name;
    private final String domain;
    private final String applicationId;
    private final String applicationSecret;
    private final Map<String, String> customParams;
    private final Set<Scope> scopes;
    private final Category category;
    private final String description;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public OpenIdConnectAuthProviderTypeResponse(@JsonProperty(AUTH_PROVIDER_TYPE_ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(DOMAIN) String domain,
        @JsonProperty(APPLICATION_ID) String applicationId,
        @JsonProperty(APPLICATION_SECRET) String applicationSecret,
        @JsonProperty(CUSTOM_PARAMS) Map<String, String> customParams,
        @JsonProperty(SCOPES) Set<Scope> scopes,
        @JsonProperty(CATEGORY) Category category,
        @Nullable @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.applicationId = applicationId;
        this.applicationSecret = applicationSecret;
        this.customParams = customParams;
        this.scopes = scopes;
        this.category = category;
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

    @JsonProperty(DOMAIN)
    public String getDomain() {
        return domain;
    }

    @JsonProperty(APPLICATION_ID)
    public String getApplicationId() {
        return applicationId;
    }

    @JsonProperty(APPLICATION_SECRET)
    public String getApplicationSecret() {
        return applicationSecret;
    }

    @JsonProperty(CUSTOM_PARAMS)
    public Map<String, String> getCustomParams() {
        return customParams;
    }

    @JsonProperty(SCOPES)
    public Set<Scope> getScopes() {
        return scopes;
    }

    @JsonProperty(CATEGORY)
    public Category getCategory() {
        return category;
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

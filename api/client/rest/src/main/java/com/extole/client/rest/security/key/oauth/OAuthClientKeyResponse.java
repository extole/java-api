package com.extole.client.rest.security.key.oauth;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyResponse;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class OAuthClientKeyResponse extends ClientKeyResponse {

    public static final String ALGORITHM_NAME_OAUTH = "OAUTH";

    protected static final String AUTHORIZATION_URL = "authorization_url";
    protected static final String OAUTH_CLIENT_ID = "oauth_client_id";
    protected static final String SCOPE = "scope";

    private final BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> authorizationUrl;
    private final String oAuthClientId;
    private final Optional<String> scope;

    @JsonCreator
    public OAuthClientKeyResponse(
        @JsonProperty(KEY_ID) String id,
        @JsonProperty(NAME) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(KEY) String key,
        @JsonProperty(TYPE) ClientKeyType type,
        @JsonProperty(DESCRIPTION) BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
        @JsonProperty(PARTNER_KEY_ID) String partnerKeyId,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt,
        @JsonProperty(AUTHORIZATION_URL) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> authorizationUrl,
        @JsonProperty(OAUTH_CLIENT_ID) String oAuthClientId,
        @JsonProperty(SCOPE) Optional<String> scope,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(id, name, algorithm, key, type, description, partnerKeyId, tags, createdAt, updatedAt, componentIds,
            componentReferences);
        this.authorizationUrl = authorizationUrl;
        this.oAuthClientId = oAuthClientId;
        this.scope = scope;
    }

    @JsonProperty(AUTHORIZATION_URL)
    public BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> getAuthorizationUrl() {
        return authorizationUrl;
    }

    @JsonProperty(OAUTH_CLIENT_ID)
    public String getOAuthClientId() {
        return oAuthClientId;
    }

    @JsonProperty(SCOPE)
    public Optional<String> getScope() {
        return scope;
    }

}

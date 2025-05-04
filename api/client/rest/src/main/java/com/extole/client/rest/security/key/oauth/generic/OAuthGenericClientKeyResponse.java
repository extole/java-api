package com.extole.client.rest.security.key.oauth.generic;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.OAuthClientKeyRuntimeContext;
import com.extole.api.client.security.key.OAuthRequest;
import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.api.client.security.key.response.OAuthClientKeyResponseContext;
import com.extole.api.client.security.key.response.OAuthResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyResponse;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class OAuthGenericClientKeyResponse extends OAuthClientKeyResponse {

    public static final String ALGORITHM_NAME_OAUTH_GENERIC = "OAUTH_GENERIC";

    private static final String REQUEST = "request";
    private static final String RESPONSE_HANDLER = "response_handler";

    private final BuildtimeEvaluatable<ClientKeyBuildtimeContext,
        RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>> request;
    private final BuildtimeEvaluatable<ClientKeyBuildtimeContext,
        RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>> responseHandler;

    @JsonCreator
    public OAuthGenericClientKeyResponse(
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
        @JsonProperty(REQUEST) BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>> request,
        @JsonProperty(RESPONSE_HANDLER) BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>> responseHandler,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(id, name, algorithm, key, type, description, partnerKeyId, createdAt, updatedAt, authorizationUrl,
            oAuthClientId, scope, tags, componentIds, componentReferences);
        this.request = request;
        this.responseHandler = responseHandler;
    }

    @JsonProperty(REQUEST)
    public BuildtimeEvaluatable<ClientKeyBuildtimeContext,
        RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>> getRequest() {
        return request;
    }

    @JsonProperty(RESPONSE_HANDLER)
    public BuildtimeEvaluatable<ClientKeyBuildtimeContext,
        RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>> getResponseHandler() {
        return responseHandler;
    }

}

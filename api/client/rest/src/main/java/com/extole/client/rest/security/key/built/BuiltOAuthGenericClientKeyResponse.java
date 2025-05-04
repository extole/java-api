package com.extole.client.rest.security.key.built;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.OAuthClientKeyRuntimeContext;
import com.extole.api.client.security.key.OAuthRequest;
import com.extole.api.client.security.key.response.OAuthClientKeyResponseContext;
import com.extole.api.client.security.key.response.OAuthResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltOAuthGenericClientKeyResponse extends BuiltOAuthClientKeyResponse {

    public static final String ALGORITHM_NAME_OAUTH_GENERIC = "OAUTH_GENERIC";

    private static final String REQUEST = "request";
    private static final String RESPONSE_HANDLER = "response_handler";

    private final RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest> request;
    private final RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse> responseHandler;

    @JsonCreator
    public BuiltOAuthGenericClientKeyResponse(
        @JsonProperty(KEY_ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(KEY) String key,
        @JsonProperty(TYPE) ClientKeyType type,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(PARTNER_KEY_ID) String partnerKeyId,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt,
        @JsonProperty(AUTHORIZATION_URL) String authorizationUrl,
        @JsonProperty(OAUTH_CLIENT_ID) String oAuthClientId,
        @JsonProperty(SCOPE) Optional<String> scope,
        @JsonProperty(REQUEST) RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest> request,
        @JsonProperty(RESPONSE_HANDLER) RuntimeEvaluatable<OAuthClientKeyResponseContext,
            OAuthResponse> responseHandler,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(id, name, algorithm, key, type, description, partnerKeyId, createdAt, updatedAt, authorizationUrl,
            oAuthClientId, scope, tags, componentIds, componentReferences);
        this.request = request;
        this.responseHandler = responseHandler;
    }

    @JsonProperty(REQUEST)
    public RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest> getRequest() {
        return request;
    }

    @JsonProperty(RESPONSE_HANDLER)
    public RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse> getResponseHandler() {
        return responseHandler;
    }

}

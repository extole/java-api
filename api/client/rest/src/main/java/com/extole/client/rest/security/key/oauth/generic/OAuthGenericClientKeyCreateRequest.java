package com.extole.client.rest.security.key.oauth.generic;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.OAuthClientKeyRuntimeContext;
import com.extole.api.client.security.key.OAuthRequest;
import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.api.client.security.key.response.OAuthClientKeyResponseContext;
import com.extole.api.client.security.key.response.OAuthResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyCreateRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class OAuthGenericClientKeyCreateRequest extends OAuthClientKeyCreateRequest {

    public static final String ALGORITHM_NAME_OAUTH_GENERIC = "OAUTH_GENERIC";

    private static final String REQUEST = "request";
    private static final String RESPONSE_HANDLER = "response_handler";

    private final Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
        RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>>> request;
    private final Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
        RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>>> responseHandler;

    @JsonCreator
    public OAuthGenericClientKeyCreateRequest(@JsonProperty(TYPE) ClientKeyType type,
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(KEY) String key,
        @JsonProperty(NAME) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        @JsonProperty(DESCRIPTION) BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
        @JsonProperty(PARTNER_KEY_ID) Optional<String> partnerKeyId,
        @JsonProperty(AUTHORIZATION_URL) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> authorizationUrl,
        @JsonProperty(OAUTH_CLIENT_ID) String oAuthClientId,
        @JsonProperty(SCOPE) Optional<String> scope,
        @JsonProperty(REQUEST) Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>>> request,
        @JsonProperty(RESPONSE_HANDLER) Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>>> responseHandler,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(type, algorithm, key, name, description, partnerKeyId, authorizationUrl, oAuthClientId, scope, tags,
            componentIds, componentReferences);
        this.request = request;
        this.responseHandler = responseHandler;
    }

    @JsonProperty(REQUEST)
    public
        Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>>>
        getRequest() {
        return request;
    }

    @JsonProperty(RESPONSE_HANDLER)
    public
        Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>>>
        getResponseHandler() {
        return responseHandler;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends OAuthClientKeyCreateRequest.Builder {

        private Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>>> request =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>>> responseHandler =
                Omissible.omitted();

        private Builder() {
        }

        public Builder withRequest(
            BuildtimeEvaluatable<ClientKeyBuildtimeContext,
                RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>> request) {
            this.request = Omissible.of(request);
            return this;
        }

        public Builder withResponseHandler(
            BuildtimeEvaluatable<ClientKeyBuildtimeContext,
                RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>> responseHandler) {
            this.responseHandler = Omissible.of(responseHandler);
            return this;
        }

        @Override
        public OAuthGenericClientKeyCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new OAuthGenericClientKeyCreateRequest(type, ClientKeyAlgorithm.OAUTH_GENERIC, key, name,
                description, partnerKeyId, authorizationUrl, oAuthClientId, scope, request, responseHandler, tags,
                componentIds, componentReferences);
        }

    }

}

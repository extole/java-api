package com.extole.client.rest.security.key.oauth.generic;

import java.util.List;
import java.util.Optional;
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
import com.extole.client.rest.security.key.oauth.OAuthClientKeyUpdateRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class OAuthGenericClientKeyUpdateRequest extends OAuthClientKeyUpdateRequest {

    public static final String ALGORITHM_NAME_OAUTH_GENERIC = "OAUTH_GENERIC";

    private static final String REQUEST = "request";
    private static final String RESPONSE_HANDLER = "response_handler";

    private final Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
        RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>>> request;
    private final Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
        RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>>> responseHandler;

    @JsonCreator
    public OAuthGenericClientKeyUpdateRequest(
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(NAME) Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> name,
        @JsonProperty(DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>>> description,
        @JsonProperty(KEY) Omissible<String> key,
        @JsonProperty(OAUTH_CLIENT_ID) Omissible<String> oAuthClientId,
        @JsonProperty(AUTHORIZATION_URL) Omissible<
            BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> authorizationUrl,
        @JsonProperty(SCOPE) Omissible<Optional<String>> scope,
        @JsonProperty(REQUEST) Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyRuntimeContext, OAuthRequest>>> request,
        @JsonProperty(RESPONSE_HANDLER) Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext,
            RuntimeEvaluatable<OAuthClientKeyResponseContext, OAuthResponse>>> responseHandler,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(PARTNER_KEY_ID) Omissible<String> partnerKeyId) {
        super(algorithm, name, description, key, oAuthClientId, authorizationUrl, scope, componentIds,
            componentReferences, partnerKeyId);
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

    public static final class Builder extends OAuthClientKeyUpdateRequest.Builder {

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
        public OAuthGenericClientKeyUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new OAuthGenericClientKeyUpdateRequest(ClientKeyAlgorithm.OAUTH_GENERIC, name, description, key,
                oAuthClientId, authorizationUrl, scope, request, responseHandler, componentIds,
                componentReferences, partnerKeyId);
        }

    }

}

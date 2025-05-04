package com.extole.client.rest.security.key.oauth.optimove;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyCreateRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class OAuthOptimoveClientKeyCreateRequest extends OAuthClientKeyCreateRequest {

    public static final String ALGORITHM_NAME_OAUTH_OPTIMOVE = "OAUTH_OPTIMOVE";

    @JsonCreator
    public OAuthOptimoveClientKeyCreateRequest(
        @JsonProperty(TYPE) ClientKeyType type,
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(KEY) String key,
        @JsonProperty(NAME) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        @JsonProperty(DESCRIPTION) BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
        @JsonProperty(PARTNER_KEY_ID) Optional<String> partnerKeyId,
        @JsonProperty(AUTHORIZATION_URL) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> authorizationUrl,
        @JsonProperty(OAUTH_CLIENT_ID) String oAuthClientId,
        @JsonProperty(SCOPE) Optional<String> scope,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(type, algorithm, key, name, description, partnerKeyId, authorizationUrl, oAuthClientId, scope, tags,
            componentIds, componentReferences);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends OAuthClientKeyCreateRequest.Builder {

        @Override
        public OAuthOptimoveClientKeyCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new OAuthOptimoveClientKeyCreateRequest(type, ClientKeyAlgorithm.OAUTH_OPTIMOVE, key, name,
                description, partnerKeyId, authorizationUrl, oAuthClientId, scope, tags, componentIds,
                componentReferences);
        }

    }

}

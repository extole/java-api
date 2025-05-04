package com.extole.client.rest.security.key.oauth.sfdc.password;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyUpdateRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class OAuthSfdcPasswordClientKeyUpdateRequest extends OAuthClientKeyUpdateRequest {

    public static final String ALGORITHM_NAME_OAUTH_SFDC_PASSWORD = "OAUTH_SFDC_PASSWORD";

    @JsonCreator
    public OAuthSfdcPasswordClientKeyUpdateRequest(
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(NAME) Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> name,
        @JsonProperty(DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>>> description,
        @JsonProperty(KEY) Omissible<String> key,
        @JsonProperty(OAUTH_CLIENT_ID) Omissible<String> oAuthClientId,
        @JsonProperty(AUTHORIZATION_URL) Omissible<
            BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> authorizationUrl,
        @JsonProperty(SCOPE) Omissible<Optional<String>> scope,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(PARTNER_KEY_ID) Omissible<String> partnerKeyId) {
        super(algorithm, name, description, key, oAuthClientId, authorizationUrl, scope, componentIds,
            componentReferences, partnerKeyId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends OAuthClientKeyUpdateRequest.Builder {

        private Builder() {
        }

        @Override
        public OAuthSfdcPasswordClientKeyUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new OAuthSfdcPasswordClientKeyUpdateRequest(ClientKeyAlgorithm.OAUTH_SFDC_PASSWORD, name,
                description, key, oAuthClientId, authorizationUrl, scope, componentIds,
                componentReferences, partnerKeyId);
        }

    }

}

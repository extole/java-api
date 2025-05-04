package com.extole.client.rest.security.key.oauth;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyUpdateRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class OAuthClientKeyUpdateRequest extends ClientKeyUpdateRequest {

    public static final String ALGORITHM_NAME_OAUTH = "OAUTH";

    protected static final String KEY = "key";
    protected static final String OAUTH_CLIENT_ID = "oauth_client_id";
    protected static final String AUTHORIZATION_URL = "authorization_url";
    protected static final String SCOPE = "scope";

    private final Omissible<String> key;
    private final Omissible<String> oAuthClientId;
    private final Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> authorizationUrl;
    private final Omissible<Optional<String>> scope;

    @JsonCreator
    public OAuthClientKeyUpdateRequest(
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
        super(algorithm, name, description, componentIds, componentReferences, partnerKeyId);
        this.key = key;
        this.oAuthClientId = oAuthClientId;
        this.authorizationUrl = authorizationUrl;
        this.scope = scope;
    }

    @JsonProperty(KEY)
    public Omissible<String> getKey() {
        return key;
    }

    @JsonProperty(OAUTH_CLIENT_ID)
    public Omissible<String> getOAuthClientId() {
        return oAuthClientId;
    }

    @JsonProperty(AUTHORIZATION_URL)
    public Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> getAuthorizationUrl() {
        return authorizationUrl;
    }

    @JsonProperty(SCOPE)
    public Omissible<Optional<String>> getScope() {
        return scope;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ClientKeyUpdateRequest.Builder<OAuthClientKeyUpdateRequest, Builder> {

        protected Omissible<String> key = Omissible.omitted();
        protected Omissible<String> oAuthClientId = Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> authorizationUrl =
            Omissible.omitted();
        protected Omissible<Optional<String>> scope = Omissible.omitted();

        protected Builder() {
        }

        public Builder withKey(String key) {
            this.key = Omissible.of(key);
            return this;
        }

        public Builder withOAuthClientId(String oAuthClientId) {
            this.oAuthClientId = Omissible.of(oAuthClientId);
            return this;
        }

        public Builder withAuthorizationUrl(BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> authorizationUrl) {
            this.authorizationUrl = Omissible.of(authorizationUrl);
            return this;
        }

        public Builder withScope(Optional<String> scope) {
            this.scope = Omissible.of(scope);
            return this;
        }

        @Override
        public OAuthClientKeyUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new OAuthClientKeyUpdateRequest(ClientKeyAlgorithm.OAUTH, name, description, key, oAuthClientId,
                authorizationUrl, scope, componentIds, componentReferences, partnerKeyId);
        }

    }

}

package com.extole.client.rest.security.key.oauth.lead.perfection;

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

public class OAuthLeadPerfectionClientKeyCreateRequest extends OAuthClientKeyCreateRequest {

    public static final String ALGORITHM_NAME_OAUTH_LEAD_PERFECTION = "OAUTH_LEAD_PERFECTION";

    private static final String LEAD_PERFECTION_CLIENT_ID = "lead_perfection_client_id";
    private static final String APP_KEY = "app_key";

    private final Omissible<String> leadPerfectionClientId;
    private final Omissible<String> appKey;

    @JsonCreator
    public OAuthLeadPerfectionClientKeyCreateRequest(
        @JsonProperty(TYPE) ClientKeyType type,
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(KEY) String key,
        @JsonProperty(NAME) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        @JsonProperty(DESCRIPTION) BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
        @JsonProperty(PARTNER_KEY_ID) Optional<String> partnerKeyId,
        @JsonProperty(AUTHORIZATION_URL) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> authorizationUrl,
        @JsonProperty(OAUTH_CLIENT_ID) String oAuthClientId,
        @JsonProperty(SCOPE) Optional<String> scope,
        @JsonProperty(LEAD_PERFECTION_CLIENT_ID) Omissible<String> leadPerfectionClientId,
        @JsonProperty(APP_KEY) Omissible<String> appKey,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(type, algorithm, key, name, description, partnerKeyId, authorizationUrl, oAuthClientId, scope, tags,
            componentIds, componentReferences);
        this.leadPerfectionClientId = leadPerfectionClientId;
        this.appKey = appKey;
    }

    @JsonProperty(LEAD_PERFECTION_CLIENT_ID)
    public Omissible<String> getLeadPerfectionClientId() {
        return leadPerfectionClientId;
    }

    @JsonProperty(APP_KEY)
    public Omissible<String> getAppKey() {
        return appKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends OAuthClientKeyCreateRequest.Builder {

        private Omissible<String> leadPerfectionClientId = Omissible.omitted();
        private Omissible<String> appKey = Omissible.omitted();

        public Builder withLeadPerfectionClientId(String leadPerfectionClientId) {
            this.leadPerfectionClientId = Omissible.of(leadPerfectionClientId);
            return this;
        }

        public Builder withAppKey(String appKey) {
            this.appKey = Omissible.of(appKey);
            return this;
        }

        @Override
        public OAuthLeadPerfectionClientKeyCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new OAuthLeadPerfectionClientKeyCreateRequest(type, ClientKeyAlgorithm.OAUTH_LEAD_PERFECTION, key,
                name, description, partnerKeyId, authorizationUrl, oAuthClientId, scope, leadPerfectionClientId, appKey,
                tags, componentIds,
                componentReferences);
        }

    }

}

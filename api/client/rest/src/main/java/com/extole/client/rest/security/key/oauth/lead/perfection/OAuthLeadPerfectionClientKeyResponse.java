package com.extole.client.rest.security.key.oauth.lead.perfection;

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
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyResponse;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class OAuthLeadPerfectionClientKeyResponse extends OAuthClientKeyResponse {

    public static final String ALGORITHM_NAME_OAUTH_LEAD_PERFECTION = "OAUTH_LEAD_PERFECTION";

    private static final String LEAD_PERFECTION_CLIENT_ID = "lead_perfection_client_id";
    private static final String APP_KEY = "app_key";

    private final String leadPerfectionClientId;
    private final String appKey;

    @JsonCreator
    public OAuthLeadPerfectionClientKeyResponse(
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
        @JsonProperty(LEAD_PERFECTION_CLIENT_ID) String leadPerfectionClientId,
        @JsonProperty(APP_KEY) String appKey,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(id, name, algorithm, key, type, description, partnerKeyId, createdAt, updatedAt, authorizationUrl,
            oAuthClientId, scope, tags, componentIds, componentReferences);
        this.leadPerfectionClientId = leadPerfectionClientId;
        this.appKey = appKey;
    }

    public String getLeadPerfectionClientId() {
        return leadPerfectionClientId;
    }

    public String getAppKey() {
        return appKey;
    }
}

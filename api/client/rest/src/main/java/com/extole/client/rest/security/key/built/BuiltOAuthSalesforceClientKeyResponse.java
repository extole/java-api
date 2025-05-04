package com.extole.client.rest.security.key.built;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.id.Id;

public class BuiltOAuthSalesforceClientKeyResponse extends BuiltOAuthClientKeyResponse {

    public static final String ALGORITHM_NAME_OAUTH_SALESFORCE = "OAUTH_SALESFORCE";

    private static final String ACCOUNT_ID = "account_id";

    private final Optional<Integer> accountId;

    @JsonCreator
    public BuiltOAuthSalesforceClientKeyResponse(
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
        @JsonProperty(ACCOUNT_ID) Optional<Integer> accountId,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(id, name, algorithm, key, type, description, partnerKeyId, createdAt, updatedAt, authorizationUrl,
            oAuthClientId, scope, tags, componentIds, componentReferences);
        this.accountId = accountId;
    }

    @JsonProperty(ACCOUNT_ID)
    public Optional<Integer> getAccountId() {
        return accountId;
    }

}

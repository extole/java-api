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

public class BuiltSslPkcs12ClientKeyResponse extends BuiltClientKeyResponse {

    public static final String ALGORITHM_NAME_SSL_PKCS_12 = "SSL_PKCS_12";
    private static final String PASSWORD = "password";

    private final Optional<String> password;

    @JsonCreator
    public BuiltSslPkcs12ClientKeyResponse(
        @JsonProperty(KEY_ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(KEY) String key,
        @JsonProperty(PASSWORD) Optional<String> password,
        @JsonProperty(TYPE) ClientKeyType type,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(PARTNER_KEY_ID) String partnerKeyId,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(id, name, algorithm, key, type, description, partnerKeyId, tags, createdAt, updatedAt, componentIds,
            componentReferences);
        this.password = password;
    }

    @JsonProperty(PASSWORD)
    public Optional<String> getPassword() {
        return password;
    }
}

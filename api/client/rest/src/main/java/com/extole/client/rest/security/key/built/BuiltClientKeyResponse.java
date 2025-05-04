package com.extole.client.rest.security.key.built;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableSet;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BuiltClientKeyResponse.ALGORITHM, visible = true)
@JsonSubTypes({
    @Type(value = BuiltGenericClientKeyResponse.class,
        names = {"HS256", "HS384", "HS512", "RS256", "RS384", "RS512", "ES256", "ES384", "ES512", "PS256", "PS384",
            "PS512", "RSA", "PASSWORD", "HTTP_BASIC", "A128KW", "A192KW", "A256KW", "RSA_OAEP_256", "RSA_OAEP_384",
            "RSA_OAEP_512"}),
    @Type(value = BuiltOAuthClientKeyResponse.class,
        name = BuiltOAuthClientKeyResponse.ALGORITHM_NAME_OAUTH),
    @Type(value = BuiltOAuthSalesforceClientKeyResponse.class,
        name = BuiltOAuthSalesforceClientKeyResponse.ALGORITHM_NAME_OAUTH_SALESFORCE),
    @Type(value = BuiltOAuthGenericClientKeyResponse.class,
        name = BuiltOAuthGenericClientKeyResponse.ALGORITHM_NAME_OAUTH_GENERIC),
    @Type(value = BuiltOAuthListrakClientKeyResponse.class,
        name = BuiltOAuthListrakClientKeyResponse.ALGORITHM_NAME_OAUTH_LISTRAK),
    @Type(value = BuiltOAuthOptimoveClientKeyResponse.class,
        name = BuiltOAuthOptimoveClientKeyResponse.ALGORITHM_NAME_OAUTH_OPTIMOVE),
    @Type(value = BuiltOAuthLeadPerfectionClientKeyResponse.class,
        name = BuiltOAuthLeadPerfectionClientKeyResponse.ALGORITHM_NAME_OAUTH_LEAD_PERFECTION),
    @Type(value = BuiltOAuthSfdcClientKeyResponse.class,
        name = BuiltOAuthSfdcClientKeyResponse.ALGORITHM_NAME_OAUTH_SFDC),
    @Type(value = BuiltOAuthSfdcPasswordClientKeyResponse.class,
        name = BuiltOAuthSfdcPasswordClientKeyResponse.ALGORITHM_NAME_OAUTH_SFDC_PASSWORD),
    @Type(value = BuiltSslPkcs12ClientKeyResponse.class,
        name = BuiltSslPkcs12ClientKeyResponse.ALGORITHM_NAME_SSL_PKCS_12)
})
@Schema(discriminatorProperty = BuiltClientKeyResponse.ALGORITHM, discriminatorMapping = {
    @DiscriminatorMapping(value = "HS256",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "HS384",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "HS512",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RS256",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RS384",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RS512",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "ES256",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "ES384",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "ES512",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PS256",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PS384",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PS512",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PASSWORD",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "HTTP_BASIC",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "A128KW",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "A192KW",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "A256KW",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA_OAEP_256",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA_OAEP_384",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA_OAEP_512",
        schema = BuiltGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = BuiltOAuthClientKeyResponse.ALGORITHM_NAME_OAUTH,
        schema = BuiltOAuthClientKeyResponse.class),
    @DiscriminatorMapping(value = BuiltOAuthSalesforceClientKeyResponse.ALGORITHM_NAME_OAUTH_SALESFORCE,
        schema = BuiltOAuthSalesforceClientKeyResponse.class),
    @DiscriminatorMapping(value = BuiltOAuthGenericClientKeyResponse.ALGORITHM_NAME_OAUTH_GENERIC,
        schema = BuiltOAuthGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = BuiltOAuthListrakClientKeyResponse.ALGORITHM_NAME_OAUTH_LISTRAK,
        schema = BuiltOAuthListrakClientKeyResponse.class),
    @DiscriminatorMapping(value = BuiltOAuthOptimoveClientKeyResponse.ALGORITHM_NAME_OAUTH_OPTIMOVE,
        schema = BuiltOAuthOptimoveClientKeyResponse.class),
    @DiscriminatorMapping(value = BuiltOAuthLeadPerfectionClientKeyResponse.ALGORITHM_NAME_OAUTH_LEAD_PERFECTION,
        schema = BuiltOAuthLeadPerfectionClientKeyResponse.class),
    @DiscriminatorMapping(value = BuiltOAuthSfdcClientKeyResponse.ALGORITHM_NAME_OAUTH_SFDC,
        schema = BuiltOAuthSfdcClientKeyResponse.class),
    @DiscriminatorMapping(value = BuiltOAuthSfdcPasswordClientKeyResponse.ALGORITHM_NAME_OAUTH_SFDC_PASSWORD,
        schema = BuiltOAuthSfdcPasswordClientKeyResponse.class),
    @DiscriminatorMapping(value = BuiltSslPkcs12ClientKeyResponse.ALGORITHM_NAME_SSL_PKCS_12,
        schema = BuiltSslPkcs12ClientKeyResponse.class)
})
public class BuiltClientKeyResponse extends ComponentElementResponse {

    protected static final String KEY_ID = "id";
    protected static final String NAME = "name";
    protected static final String ALGORITHM = "algorithm";
    protected static final String KEY = "key";
    protected static final String TYPE = "type";
    protected static final String DESCRIPTION = "description";
    protected static final String PARTNER_KEY_ID = "partner_key_id";
    protected static final String CREATED_AT = "created_at";
    protected static final String UPDATED_AT = "updated_at";
    protected static final String TAGS = "tags";

    private final String id;
    private final String name;
    private final ClientKeyAlgorithm algorithm;
    private final String key;
    private final ClientKeyType type;
    private final Optional<String> description;
    private final String partnerKeyId;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final Set<String> tags;

    public BuiltClientKeyResponse(
        String id,
        String name,
        ClientKeyAlgorithm algorithm,
        String key,
        ClientKeyType type,
        Optional<String> description,
        String partnerKeyId,
        Set<String> tags,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        List<Id<ComponentResponse>> componentIds,
        List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.id = id;
        this.name = name;
        this.algorithm = algorithm;
        this.key = key;
        this.type = type;
        this.description = description;
        this.partnerKeyId = partnerKeyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tags = ImmutableSet.copyOf(tags);
    }

    @JsonProperty(KEY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(ALGORITHM)
    public ClientKeyAlgorithm getAlgorithm() {
        return algorithm;
    }

    @JsonProperty(KEY)
    public String getKey() {
        return key;
    }

    @JsonProperty(TYPE)
    public ClientKeyType getType() {
        return type;
    }

    @JsonProperty(DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(PARTNER_KEY_ID)
    public String getPartnerKeyId() {
        return partnerKeyId;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(UPDATED_AT)
    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}

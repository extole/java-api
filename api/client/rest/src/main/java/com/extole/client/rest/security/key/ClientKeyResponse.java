package com.extole.client.rest.security.key;

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

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyResponse;
import com.extole.client.rest.security.key.oauth.generic.OAuthGenericClientKeyResponse;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyResponse;
import com.extole.client.rest.security.key.oauth.listrak.OAuthListrakClientKeyResponse;
import com.extole.client.rest.security.key.oauth.optimove.OAuthOptimoveClientKeyResponse;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyResponse;
import com.extole.client.rest.security.key.oauth.sfdc.OAuthSfdcClientKeyResponse;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ClientKeyResponse.ALGORITHM, visible = true)
@JsonSubTypes({
    @Type(value = GenericClientKeyResponse.class,
        names = {"HS256", "HS384", "HS512", "RS256_PUBLIC", "RS384_PUBLIC", "RS512_PUBLIC", "RS256_PRIVATE",
            "RS384_PRIVATE", "RS512_PRIVATE", "ES256_PUBLIC", "ES384_PUBLIC", "ES512_PUBLIC", "ES256_PRIVATE",
            "ES384_PRIVATE", "ES512_PRIVATE", "PS256_PUBLIC", "PS384_PUBLIC", "PS512_PUBLIC", "PS256_PRIVATE",
            "PS384_PRIVATE", "PS512_PRIVATE", "RSA", "PASSWORD", "HTTP_BASIC", "A128KW", "A192KW", "A256KW",
            "RSA_OAEP_256_PRIVATE", "RSA_OAEP_384_PRIVATE", "RSA_OAEP_512_PRIVATE", "RSA_OAEP_256_PUBLIC",
            "RSA_OAEP_384_PUBLIC", "RSA_OAEP_512_PUBLIC"}),
    @Type(value = OAuthClientKeyResponse.class,
        name = OAuthClientKeyResponse.ALGORITHM_NAME_OAUTH),
    @Type(value = OAuthSalesforceClientKeyResponse.class,
        name = OAuthSalesforceClientKeyResponse.ALGORITHM_NAME_OAUTH_SALESFORCE),
    @Type(value = OAuthGenericClientKeyResponse.class,
        name = OAuthGenericClientKeyResponse.ALGORITHM_NAME_OAUTH_GENERIC),
    @Type(value = OAuthSfdcPasswordClientKeyResponse.class,
        name = OAuthSfdcPasswordClientKeyResponse.ALGORITHM_NAME_OAUTH_SFDC_PASSWORD),
    @Type(value = OAuthListrakClientKeyResponse.class,
        name = OAuthListrakClientKeyResponse.ALGORITHM_NAME_OAUTH_LISTRAK),
    @Type(value = OAuthOptimoveClientKeyResponse.class,
        name = OAuthOptimoveClientKeyResponse.ALGORITHM_NAME_OAUTH_OPTIMOVE),
    @Type(value = OAuthLeadPerfectionClientKeyResponse.class,
        name = OAuthLeadPerfectionClientKeyResponse.ALGORITHM_NAME_OAUTH_LEAD_PERFECTION),
    @Type(value = OAuthSfdcClientKeyResponse.class,
        name = OAuthSfdcClientKeyResponse.ALGORITHM_NAME_OAUTH_SFDC),
    @Type(value = SslPkcs12ClientKeyResponse.class,
        name = SslPkcs12ClientKeyResponse.ALGORITHM_NAME_SSL_PKCS_12)
})
@Schema(discriminatorProperty = ClientKeyResponse.ALGORITHM, discriminatorMapping = {
    @DiscriminatorMapping(value = "HS256",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "HS384",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "HS512",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RS256_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RS384_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RS512_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RS256_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RS384_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RS512_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "ES256_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "ES384_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "ES512_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "ES256_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "ES384_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "ES512_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PS256_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PS384_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PS512_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PS256_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PS384_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PS512_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "PASSWORD",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "HTTP_BASIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "A128KW",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "A192KW",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "A256KW",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA_OAEP_256_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA_OAEP_384_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA_OAEP_512_PRIVATE",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA_OAEP_256_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA_OAEP_384_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = "RSA_OAEP_512_PUBLIC",
        schema = GenericClientKeyResponse.class),
    @DiscriminatorMapping(value = OAuthClientKeyResponse.ALGORITHM_NAME_OAUTH,
        schema = OAuthClientKeyResponse.class),
    @DiscriminatorMapping(value = OAuthSalesforceClientKeyResponse.ALGORITHM_NAME_OAUTH_SALESFORCE,
        schema = OAuthSalesforceClientKeyResponse.class),
    @DiscriminatorMapping(value = OAuthGenericClientKeyResponse.ALGORITHM_NAME_OAUTH_GENERIC,
        schema = OAuthGenericClientKeyResponse.class),
    @DiscriminatorMapping(value = OAuthSfdcPasswordClientKeyResponse.ALGORITHM_NAME_OAUTH_SFDC_PASSWORD,
        schema = OAuthSfdcPasswordClientKeyResponse.class),
    @DiscriminatorMapping(value = OAuthListrakClientKeyResponse.ALGORITHM_NAME_OAUTH_LISTRAK,
        schema = OAuthListrakClientKeyResponse.class),
    @DiscriminatorMapping(value = OAuthOptimoveClientKeyResponse.ALGORITHM_NAME_OAUTH_OPTIMOVE,
        schema = OAuthOptimoveClientKeyResponse.class),
    @DiscriminatorMapping(value = OAuthLeadPerfectionClientKeyResponse.ALGORITHM_NAME_OAUTH_LEAD_PERFECTION,
        schema = OAuthLeadPerfectionClientKeyResponse.class),
    @DiscriminatorMapping(value = OAuthSfdcClientKeyResponse.ALGORITHM_NAME_OAUTH_SFDC,
        schema = OAuthSfdcClientKeyResponse.class),
    @DiscriminatorMapping(value = SslPkcs12ClientKeyResponse.ALGORITHM_NAME_SSL_PKCS_12,
        schema = SslPkcs12ClientKeyResponse.class)
})
public class ClientKeyResponse extends ComponentElementResponse {

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
    private final BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name;
    private final ClientKeyAlgorithm algorithm;
    private final String key;
    private final ClientKeyType type;
    private final BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description;
    private final String partnerKeyId;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final Set<String> tags;

    public ClientKeyResponse(
        String id,
        BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        ClientKeyAlgorithm algorithm,
        String key,
        ClientKeyType type,
        BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
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
    public BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> getName() {
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
    public BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> getDescription() {
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

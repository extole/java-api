package com.extole.client.rest.security.key;

import static com.extole.client.rest.security.key.ClientKeyCreateRequest.ALGORITHM;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.generic.OAuthGenericClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.listrak.OAuthListrakClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.optimove.OAuthOptimoveClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.sfdc.OAuthSfdcClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyCreateRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ALGORITHM, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GenericClientKeyCreateRequest.class,
        names = {"HS256", "HS384", "HS512", "RS256_PUBLIC", "RS384_PUBLIC", "RS512_PUBLIC", "RS256_PRIVATE",
            "RS384_PRIVATE", "RS512_PRIVATE", "ES256_PUBLIC", "ES384_PUBLIC", "ES512_PUBLIC", "ES256_PRIVATE",
            "ES384_PRIVATE", "ES512_PRIVATE", "PS256_PUBLIC", "PS384_PUBLIC", "PS512_PUBLIC", "PS256_PRIVATE",
            "PS384_PRIVATE", "PS512_PRIVATE", "RSA", "PASSWORD", "HTTP_BASIC", "A128KW", "A192KW", "A256KW",
            "RSA_OAEP_256_PRIVATE", "RSA_OAEP_384_PRIVATE", "RSA_OAEP_512_PRIVATE", "RSA_OAEP_256_PUBLIC",
            "RSA_OAEP_384_PUBLIC", "RSA_OAEP_512_PUBLIC"}),
    @JsonSubTypes.Type(value = OAuthClientKeyCreateRequest.class,
        name = OAuthClientKeyCreateRequest.ALGORITHM_NAME_OAUTH),
    @JsonSubTypes.Type(value = OAuthSalesforceClientKeyCreateRequest.class,
        name = OAuthSalesforceClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_SALESFORCE),
    @JsonSubTypes.Type(value = OAuthGenericClientKeyCreateRequest.class,
        name = OAuthGenericClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_GENERIC),
    @JsonSubTypes.Type(value = OAuthSfdcPasswordClientKeyCreateRequest.class,
        name = OAuthSfdcPasswordClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_SFDC_PASSWORD),
    @JsonSubTypes.Type(value = OAuthListrakClientKeyCreateRequest.class,
        name = OAuthListrakClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_LISTRAK),
    @JsonSubTypes.Type(value = OAuthOptimoveClientKeyCreateRequest.class,
        name = OAuthOptimoveClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_OPTIMOVE),
    @JsonSubTypes.Type(value = OAuthLeadPerfectionClientKeyCreateRequest.class,
        name = OAuthLeadPerfectionClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_LEAD_PERFECTION),
    @JsonSubTypes.Type(value = OAuthSfdcClientKeyCreateRequest.class,
        name = OAuthSfdcClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_SFDC),
})
@Schema(discriminatorProperty = ALGORITHM, discriminatorMapping = {
    @DiscriminatorMapping(value = "HS256",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "HS384",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "HS512",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RS256_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RS384_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RS512_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RS256_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RS384_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RS512_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "ES256_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "ES384_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "ES512_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "ES256_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "ES384_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "ES512_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "PS256_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "PS384_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "PS512_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "PS256_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "PS384_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "PS512_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RSA",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "PASSWORD",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "HTTP_BASIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "A128KW",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "A192KW",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "A256KW",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_256_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_384_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_512_PRIVATE",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_256_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_384_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_512_PUBLIC",
        schema = GenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = OAuthClientKeyCreateRequest.ALGORITHM_NAME_OAUTH,
        schema = OAuthClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = OAuthSalesforceClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_SALESFORCE,
        schema = OAuthSalesforceClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = OAuthGenericClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_GENERIC,
        schema = OAuthGenericClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = OAuthSfdcPasswordClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_SFDC_PASSWORD,
        schema = OAuthSfdcPasswordClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = OAuthListrakClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_LISTRAK,
        schema = OAuthListrakClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = OAuthOptimoveClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_OPTIMOVE,
        schema = OAuthOptimoveClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = OAuthLeadPerfectionClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_LEAD_PERFECTION,
        schema = OAuthLeadPerfectionClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = OAuthSfdcClientKeyCreateRequest.ALGORITHM_NAME_OAUTH_SFDC,
        schema = OAuthSfdcClientKeyCreateRequest.class),
    @DiscriminatorMapping(value = SslPkcs12ClientKeyCreateRequest.ALGORITHM_NAME_SSL_PKCS_12,
        schema = SslPkcs12ClientKeyCreateRequest.class)
})
public abstract class StringBasedClientKeyCreateRequest extends ClientKeyCreateRequest {

    protected static final String KEY = "key";
    private final String key;

    public StringBasedClientKeyCreateRequest(ClientKeyType type,
        ClientKeyAlgorithm algorithm,
        String key,
        BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
        Optional<String> partnerKeyId,
        Omissible<Set<String>> tags,
        Omissible<List<Id<ComponentResponse>>> componentIds,
        Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(type, algorithm, name, description, partnerKeyId, tags, componentIds, componentReferences);
        this.key = key;
    }

    @JsonProperty(KEY)
    public String getKey() {
        return key;
    }

    public abstract static class Builder<REQUEST extends StringBasedClientKeyCreateRequest,
        BUILDER extends Builder<REQUEST, BUILDER>>
        extends ClientKeyCreateRequest.Builder<REQUEST, BUILDER> {

        protected String key;

        protected Builder() {
        }

        public BUILDER withKey(String key) {
            this.key = key;
            return (BUILDER) this;
        }

        public abstract REQUEST build();

    }
}

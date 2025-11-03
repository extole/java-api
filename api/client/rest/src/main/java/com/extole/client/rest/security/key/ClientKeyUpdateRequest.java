package com.extole.client.rest.security.key;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.generic.OAuthGenericClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.listrak.OAuthListrakClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.optimove.OAuthOptimoveClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.sfdc.OAuthSfdcClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyUpdateRequest;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ClientKeyUpdateRequest.ALGORITHM, visible = true)
@JsonSubTypes({
    @Type(value = GenericClientKeyUpdateRequest.class,
        names = {"HS256", "HS384", "HS512", "RS256_PUBLIC", "RS384_PUBLIC", "RS512_PUBLIC", "RS256_PRIVATE",
            "RS384_PRIVATE", "RS512_PRIVATE", "ES256_PUBLIC", "ES384_PUBLIC", "ES512_PUBLIC", "ES256_PRIVATE",
            "ES384_PRIVATE", "ES512_PRIVATE", "PS256_PUBLIC", "PS384_PUBLIC", "PS512_PUBLIC", "PS256_PRIVATE",
            "PS384_PRIVATE", "PS512_PRIVATE", "RSA", "PASSWORD", "HTTP_BASIC", "A128KW", "A192KW", "A256KW",
            "RSA_OAEP_256_PRIVATE", "RSA_OAEP_384_PRIVATE", "RSA_OAEP_512_PRIVATE", "RSA_OAEP_256_PUBLIC",
            "RSA_OAEP_384_PUBLIC", "RSA_OAEP_512_PUBLIC"}),
    @Type(value = OAuthClientKeyUpdateRequest.class,
        name = OAuthClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH),
    @Type(value = OAuthSalesforceClientKeyUpdateRequest.class,
        name = OAuthSalesforceClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_SALESFORCE),
    @Type(value = OAuthGenericClientKeyUpdateRequest.class,
        name = OAuthGenericClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_GENERIC),
    @Type(value = OAuthSfdcPasswordClientKeyUpdateRequest.class,
        name = OAuthSfdcPasswordClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_SFDC_PASSWORD),
    @Type(value = OAuthListrakClientKeyUpdateRequest.class,
        name = OAuthListrakClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_LISTRAK),
    @Type(value = OAuthOptimoveClientKeyUpdateRequest.class,
        name = OAuthOptimoveClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_OPTIMOVE),
    @Type(value = OAuthLeadPerfectionClientKeyUpdateRequest.class,
        name = OAuthLeadPerfectionClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_LEAD_PERFECTION),
    @Type(value = OAuthSfdcClientKeyUpdateRequest.class,
        name = OAuthSfdcClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_SFDC),
    @Type(value = SslPkcs12ClientKeyUpdateRequest.class,
        name = SslPkcs12ClientKeyUpdateRequest.ALGORITHM_NAME_SSL_PKCS_12)
})
@Schema(discriminatorProperty = ClientKeyUpdateRequest.ALGORITHM, discriminatorMapping = {
    @DiscriminatorMapping(value = "HS256",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "HS384",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "HS512",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RS256_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RS384_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RS512_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RS256_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RS384_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RS512_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "ES256_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "ES384_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "ES512_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "ES256_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "ES384_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "ES512_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "PS256_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "PS384_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "PS512_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "PS256_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "PS384_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "PS512_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RSA",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "PASSWORD",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "HTTP_BASIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "A128KW",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "A192KW",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "A256KW",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_256_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_384_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_512_PRIVATE",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_256_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_384_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = "RSA_OAEP_512_PUBLIC",
        schema = GenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = OAuthClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH,
        schema = OAuthClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = OAuthSalesforceClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_SALESFORCE,
        schema = OAuthSalesforceClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = OAuthGenericClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_GENERIC,
        schema = OAuthGenericClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = OAuthSfdcPasswordClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_SFDC_PASSWORD,
        schema = OAuthSfdcPasswordClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = OAuthListrakClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_LISTRAK,
        schema = OAuthListrakClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = OAuthOptimoveClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_OPTIMOVE,
        schema = OAuthOptimoveClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = OAuthLeadPerfectionClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_LEAD_PERFECTION,
        schema = OAuthLeadPerfectionClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = OAuthSfdcClientKeyUpdateRequest.ALGORITHM_NAME_OAUTH_SFDC,
        schema = OAuthSfdcClientKeyUpdateRequest.class),
    @DiscriminatorMapping(value = SslPkcs12ClientKeyUpdateRequest.ALGORITHM_NAME_SSL_PKCS_12,
        schema = SslPkcs12ClientKeyUpdateRequest.class)

})
public abstract class ClientKeyUpdateRequest extends ComponentElementRequest {

    protected static final String ALGORITHM = "algorithm";
    protected static final String NAME = "name";
    protected static final String DESCRIPTION = "description";
    protected static final String PARTNER_KEY_ID = "partner_key_id";

    private final ClientKeyAlgorithm algorithm;
    private final Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>>> description;
    private final Omissible<String> partnerKeyId;

    public ClientKeyUpdateRequest(
        ClientKeyAlgorithm algorithm,
        Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> name,
        Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>>> description,
        Omissible<List<Id<ComponentResponse>>> componentIds,
        Omissible<List<ComponentReferenceRequest>> componentReferences,
        Omissible<String> partnerKeyId) {
        super(componentReferences, componentIds);
        this.algorithm = algorithm;
        this.name = name;
        this.description = description;
        this.partnerKeyId = partnerKeyId;
    }

    @JsonProperty(NAME)
    public Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(ALGORITHM)
    public ClientKeyAlgorithm getAlgorithm() {
        return algorithm;
    }

    @JsonProperty(DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(PARTNER_KEY_ID)
    public Omissible<String> getPartnerKeyId() {
        return partnerKeyId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public abstract static class Builder<REQUEST extends ClientKeyUpdateRequest, BUILDER extends Builder<REQUEST,
        BUILDER>>
        extends ComponentElementRequest.Builder<BUILDER> {

        protected Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> name = Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        protected Omissible<String> partnerKeyId = Omissible.omitted();

        protected Builder() {
        }

        public BUILDER withName(BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return (BUILDER) this;
        }

        public BUILDER withDescription(BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return (BUILDER) this;
        }

        public BUILDER withPartnerKeyId(String partnerKeyId) {
            this.partnerKeyId = Omissible.of(partnerKeyId);
            return (BUILDER) this;
        }

        public abstract REQUEST build();

    }

}

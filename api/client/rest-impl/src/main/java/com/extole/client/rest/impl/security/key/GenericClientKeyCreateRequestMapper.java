package com.extole.client.rest.impl.security.key;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.StringBasedClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyValidationRestException;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.builder.GenericClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.exception.ClientKeyInUseException;
import com.extole.model.service.client.security.key.exception.ExternalElementInUseException;
import com.extole.model.service.client.security.key.exception.MissingTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.UnsupportedTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.MissingOAuthClientIdOAuthClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingAppKeyOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingClientIdOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingPasswordOAuthSfdcPasswordClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingUsernameOAuthSfdcPasswordClientKeyException;

@Component
public class GenericClientKeyCreateRequestMapper
    extends BaseClientKeyCreateRequestMapper<StringBasedClientKeyCreateRequest, ClientKey> {

    public GenericClientKeyCreateRequestMapper(ClientKeyService clientKeyService, ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    protected ClientKey completeCreation(ClientKeyBuilder<?, ClientKey> clientKeyBuilder,
        StringBasedClientKeyCreateRequest createRequest, ComponentReferenceContext componentReferenceContext)
        throws BuildClientKeyRestException, OAuthClientKeyValidationRestException,
        OAuthSalesforceClientKeyValidationRestException, BuildClientKeyException, MissingClientKeyAlgorithmException,
        DuplicateClientKeyPartnerKeyIdException, MissingClientKeyException, InvalidClientKeyException,
        ShortClientKeyException, OAuthLeadPerfectionClientKeyValidationRestException, ClientKeyInUseException,
        UnsupportedTypeClientKeyException, MissingTypeClientKeyException, MissingOAuthClientIdOAuthClientKeyException,
        MissingClientIdOAuthLeadPerfectionClientKeyException, MissingAppKeyOAuthLeadPerfectionClientKeyException,
        InvalidComponentReferenceException, OAuthSfdcPasswordClientKeyValidationRestException,
        MissingPasswordOAuthSfdcPasswordClientKeyException, MissingUsernameOAuthSfdcPasswordClientKeyException,
        MoreThanOneComponentReferenceException, ExternalElementInUseException, ExternalElementRestException,
        OAuthClientKeyBuildRestException {
        GenericClientKeyBuilder genericClientKeyBuilder = (GenericClientKeyBuilder) clientKeyBuilder;

        if (createRequest.getAlgorithm() != null) {
            genericClientKeyBuilder.withAlgorithm(ClientKey.Algorithm.valueOf(createRequest.getAlgorithm().name()));
        }

        return super.completeCreation(genericClientKeyBuilder, createRequest, componentReferenceContext);
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.GENERIC;
    }

}

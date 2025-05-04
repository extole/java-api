package com.extole.client.rest.impl.security.key.oauth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.BuildClientKeyExceptionMapper;
import com.extole.client.rest.impl.campaign.OAuthClientKeyBuildRestExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.security.key.BaseClientKeyCreateRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.security.key.OAuthClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.builder.OAuthClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.exception.ClientKeyInUseException;
import com.extole.model.service.client.security.key.exception.ExternalElementInUseException;
import com.extole.model.service.client.security.key.exception.MissingTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.UnsupportedTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.MissingOAuthClientIdOAuthClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.TooLongOAuthClientIdOAuthClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.TooLongScopeOAuthClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingAppKeyOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingClientIdOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingPasswordOAuthSfdcPasswordClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingUsernameOAuthSfdcPasswordClientKeyException;

@Component
public class OAuthClientKeyCreateRequestMapper<REQUEST extends OAuthClientKeyCreateRequest, KEY extends OAuthClientKey>
    extends BaseClientKeyCreateRequestMapper<REQUEST, KEY> {

    @Autowired
    public OAuthClientKeyCreateRequestMapper(ClientKeyService clientKeyService, ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    protected KEY completeCreation(ClientKeyBuilder<?, KEY> clientKeyBuilder, REQUEST createRequest,
        ComponentReferenceContext componentReferenceContext)
        throws OAuthClientKeyValidationRestException, OAuthSalesforceClientKeyValidationRestException,
        BuildClientKeyRestException, BuildClientKeyException, MissingClientKeyAlgorithmException,
        DuplicateClientKeyPartnerKeyIdException, MissingClientKeyException, InvalidClientKeyException,
        ShortClientKeyException, OAuthLeadPerfectionClientKeyValidationRestException, ClientKeyInUseException,
        UnsupportedTypeClientKeyException, MissingTypeClientKeyException,
        MissingClientIdOAuthLeadPerfectionClientKeyException, MissingAppKeyOAuthLeadPerfectionClientKeyException,
        InvalidComponentReferenceException, OAuthSfdcPasswordClientKeyValidationRestException,
        MissingPasswordOAuthSfdcPasswordClientKeyException, MissingUsernameOAuthSfdcPasswordClientKeyException,
        MoreThanOneComponentReferenceException, ExternalElementRestException, OAuthClientKeyBuildRestException {
        OAuthClientKeyBuilder oAuthClientKeyBuilder = (OAuthClientKeyBuilder) clientKeyBuilder;

        oAuthClientKeyBuilder.withAuthorizationUrl(createRequest.getAuthorizationUrl());

        if (StringUtils.isNotBlank(createRequest.getOAuthClientId())) {
            try {
                oAuthClientKeyBuilder.withOAuthClientId(createRequest.getOAuthClientId());
            } catch (TooLongOAuthClientIdOAuthClientKeyException e) {
                throw RestExceptionBuilder.newBuilder(OAuthClientKeyValidationRestException.class)
                    .withErrorCode(OAuthClientKeyValidationRestException.OAUTH_CLIENT_ID_TOO_LONG)
                    .addParameter("oauth_client_id", e.getOAuthClientId())
                    .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            }
        }

        if (createRequest.getScope().isPresent() && StringUtils.isNotBlank(createRequest.getScope().get())) {
            try {
                oAuthClientKeyBuilder.withScope(createRequest.getScope().get());
            } catch (TooLongScopeOAuthClientKeyException e) {
                throw RestExceptionBuilder.newBuilder(OAuthClientKeyValidationRestException.class)
                    .withErrorCode(OAuthClientKeyValidationRestException.SCOPE_TOO_LONG)
                    .addParameter("scope", e.getScope())
                    .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            }
        }

        try {
            return (KEY) super.completeCreation(oAuthClientKeyBuilder, createRequest, componentReferenceContext);
        } catch (BuildClientKeyException e) {
            throw OAuthClientKeyBuildRestExceptionMapper.getInstance().map(e)
                .orElseThrow(() -> BuildClientKeyExceptionMapper.getInstance().map(e));
        } catch (MissingOAuthClientIdOAuthClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(OAuthClientKeyValidationRestException.class)
                .withErrorCode(OAuthClientKeyValidationRestException.MISSING_OAUTH_CLIENT_ID)
                .withCause(e)
                .build();
        } catch (ExternalElementInUseException e) {
            throw RestExceptionBuilder.newBuilder(ExternalElementRestException.class)
                .withErrorCode(ExternalElementRestException.EXTERNAL_ELEMENT_IN_USE)
                .addParameter("external_element_id", e.getElementId())
                .addParameter("entity_type", e.getEntityType())
                .addParameter("associations", e.getAssociations())
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.OAUTH;
    }

}
